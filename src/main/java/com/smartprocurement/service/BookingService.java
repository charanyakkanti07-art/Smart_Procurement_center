package com.smartprocurement.service;

import com.smartprocurement.dto.BookingRequest;
import com.smartprocurement.dto.BookingResponse;
import com.smartprocurement.dto.RescheduleRequest;
import com.smartprocurement.entity.*;
import com.smartprocurement.exception.BadRequestException;
import com.smartprocurement.exception.CapacityExceededException;
import com.smartprocurement.exception.ResourceNotFoundException;
import com.smartprocurement.repository.BookingRepository;
import com.smartprocurement.repository.CentreRepository;
import com.smartprocurement.repository.CropRepository;
import com.smartprocurement.repository.FarmerRepository;
import com.smartprocurement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private CentreRepository centreRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JourneyMonitoringService journeyMonitoringService;

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        Farmer farmer = farmerRepository.findById(request.getFarmerId())
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with id: " + request.getFarmerId()));

        ProcurementCentre centre = centreRepository.findById(request.getCentreId())
                .orElseThrow(() -> new ResourceNotFoundException("Centre not found with id: " + request.getCentreId()));

        Crop crop = cropRepository.findById(request.getCropId())
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found with id: " + request.getCropId()));

        if (!crop.getFarmer().getFarmerId().equals(farmer.getFarmerId())) {
            throw new BadRequestException("Crop does not belong to farmer with id: " + request.getFarmerId());
        }

        double requestedQuantity = request.getQuantity();
        double availableCapacity = centre.getAvailableCapacity();

        // STEP 15 — Capacity Validation
        if (requestedQuantity > availableCapacity) {
            String reason = String.format("Centre is overloaded. Available capacity is %.1f kg.", availableCapacity);
            throw new CapacityExceededException(reason, availableCapacity);
        }

        // Update centre load
        double newLoad = centre.getCurrentLoad() + requestedQuantity;
        centre.setCurrentLoad(newLoad);
        if (newLoad >= centre.getTotalCapacity()) {
            centre.setStatus(CentreStatus.OVERLOADED);
        }
        centreRepository.save(centre);

        // Create booking record
        Booking booking = Booking.builder()
                .farmer(farmer)
                .centre(centre)
                .crop(crop)
                .quantity(requestedQuantity)
                .bookingDate(request.getBookingDate())
                .slot(request.getSlot())
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking saved = bookingRepository.save(booking);

        // Schedule Automatic Journey Monitoring
        try {
            journeyMonitoringService.scheduleJourney(saved);
        } catch (Exception ignored) {
        }

        // TRIGGER EVENT: BOOKING_CONFIRMED
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("bookingId", saved.getBookingId());
            params.put("centreId", centre.getCentreId());
            params.put("centreName", centre.getName());
            params.put("bookingDate", saved.getBookingDate() != null ? saved.getBookingDate().toString() : "Today");
            params.put("slot", saved.getSlot() != null ? saved.getSlot() : "Morning Slot");
            params.put("quantity", saved.getQuantity());
            params.put("cropType", crop.getCropType());
            notificationService.sendNotification(farmer.getFarmerId(), NotificationEventType.BOOKING_CONFIRMED, params, "booking_confirm_" + saved.getBookingId());
        } catch (Exception e) {
            // Log but don't fail business transaction
        }


        return mapToResponse(saved, "Booking created successfully");
    }

    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return mapToResponse(booking, "Booking fetched successfully");
    }

    public List<BookingResponse> getBookingsByFarmer(Long farmerId) {
        if (!farmerRepository.existsById(farmerId)) {
            throw new ResourceNotFoundException("Farmer not found with id: " + farmerId);
        }
        return bookingRepository.findByFarmerFarmerId(farmerId).stream()
                .map(b -> mapToResponse(b, null))
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return mapToResponse(booking, "Booking is already cancelled");
        }

        ProcurementCentre centre = booking.getCentre();
        double updatedLoad = Math.max(0.0, centre.getCurrentLoad() - booking.getQuantity());
        centre.setCurrentLoad(updatedLoad);
        if (updatedLoad < centre.getTotalCapacity() && centre.getStatus() == CentreStatus.OVERLOADED) {
            centre.setStatus(CentreStatus.ACTIVE);
        }
        centreRepository.save(centre);

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updated = bookingRepository.save(booking);

        // TRIGGER EVENT: CANCELLATION_REQUEST
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("bookingId", updated.getBookingId());
            notificationService.sendNotification(updated.getFarmer().getFarmerId(), NotificationEventType.CANCELLATION_REQUEST, params, "booking_cancel_req_" + updated.getBookingId());
        } catch (Exception ignored) {
        }

        return mapToResponse(updated, "Booking cancelled successfully");
    }

    @Transactional
    public BookingResponse rescheduleBooking(Long id, RescheduleRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Cannot reschedule a cancelled booking");
        }

        booking.setBookingDate(request.getBookingDate());
        booking.setSlot(request.getSlot());
        booking.setStatus(BookingStatus.RESCHEDULED);

        Booking updated = bookingRepository.save(booking);

        // TRIGGER EVENT: RESCHEDULE_REQUEST
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("bookingDate", updated.getBookingDate() != null ? updated.getBookingDate().toString() : "Date");
            params.put("slot", updated.getSlot() != null ? updated.getSlot() : "Slot");
            notificationService.sendNotification(updated.getFarmer().getFarmerId(), NotificationEventType.RESCHEDULE_REQUEST, params, "booking_reschedule_req_" + updated.getBookingId() + "_" + updated.getSlot());
        } catch (Exception ignored) {
        }

        return mapToResponse(updated, "Booking rescheduled successfully");
    }

    @Transactional
    public BookingResponse startTravelling(Long id, Double latitude, Double longitude) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        booking.setStatus(BookingStatus.TRAVELLING);
        if (latitude != null) booking.setFarmerLatitude(latitude);
        if (longitude != null) booking.setFarmerLongitude(longitude);
        booking.setTravellingStartedAt(java.time.LocalDateTime.now());

        Booking updated = bookingRepository.save(booking);
        return mapToResponse(updated, "Farmer started travelling to procurement centre");
    }

    private BookingResponse mapToResponse(Booking booking, String message) {
        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .farmerId(booking.getFarmer().getFarmerId())
                .farmerName(booking.getFarmer().getName())
                .centreId(booking.getCentre().getCentreId())
                .centreName(booking.getCentre().getName())
                .cropId(booking.getCrop().getCropId())
                .cropType(booking.getCrop().getCropType())
                .quantity(booking.getQuantity())
                .bookingDate(booking.getBookingDate())
                .slot(booking.getSlot())
                .status(booking.getStatus())
                .reason(message)
                .availableCapacity(booking.getCentre().getAvailableCapacity())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
