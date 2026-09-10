package com.smartprocurement.service;

import com.smartprocurement.dto.AssignSlotRequest;
import com.smartprocurement.dto.BookingResponse;
import com.smartprocurement.dto.OwnerDashboardDTO;
import com.smartprocurement.entity.*;
import com.smartprocurement.exception.BadRequestException;
import com.smartprocurement.exception.ResourceNotFoundException;
import com.smartprocurement.repository.BookingRepository;
import com.smartprocurement.repository.CentreRepository;
import com.smartprocurement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.smartprocurement.service.NotificationService;
import java.util.HashMap;
import java.util.Map;

@Service
public class OwnerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CentreRepository centreRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private NotificationService notificationService;

    private ProcurementCentre getOwnerCentre(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElse(null);

        ProcurementCentre centre = null;
        if (user != null) {
            centre = user.getCentre();
        }

        if (centre == null) {
            centre = centreRepository.findAll().stream().findFirst()
                    .orElseGet(() -> {
                        ProcurementCentre newCentre = ProcurementCentre.builder()
                                .name("Centre A")
                                .location("Hyderabad")
                                .latitude(17.385)
                                .longitude(78.4867)
                                .totalCapacity(1000.0)
                                .currentLoad(0.0)
                                .status(CentreStatus.ACTIVE)
                                .build();
                        return centreRepository.save(newCentre);
                    });
            if (user != null) {
                user.setCentre(centre);
                userRepository.save(user);
            }
        }
        return centre;
    }

    public OwnerDashboardDTO getDashboardData(String phone) {
        ProcurementCentre centre = getOwnerCentre(phone);
        List<Booking> allBookings = bookingRepository.findByCentreCentreId(centre.getCentreId());

        long todaysFarmersCount = 0;
        long waitingCount = 0;
        long calledCount = 0;
        long arrivedCount = 0;
        long processingCount = 0;
        long completedCount = 0;
        long cancelledCount = 0;
        long reschedulingCount = 0;
        long pendingApprovalsCount = 0;

        BookingResponse currentProcessingDTO = null;
        List<BookingResponse> queueList = new ArrayList<>();
        List<BookingResponse> todaysFarmers = new ArrayList<>();
        List<BookingResponse> cancellationRequests = new ArrayList<>();
        List<BookingResponse> reschedulingRequests = new ArrayList<>();
        List<BookingResponse> pendingApprovals = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (Booking b : allBookings) {
            BookingStatus st = b.getStatus();
            BookingResponse responseDTO = mapToResponse(b);

            if (b.getBookingDate() != null && b.getBookingDate().equals(today)) {
                todaysFarmersCount++;
                todaysFarmers.add(responseDTO);
            }

            if (st == BookingStatus.WAITING || st == BookingStatus.CONFIRMED || st == BookingStatus.BOOKED) {
                waitingCount++;
                queueList.add(responseDTO);
            } else if (st == BookingStatus.CALLED) {
                calledCount++;
                queueList.add(responseDTO);
            } else if (st == BookingStatus.ARRIVED) {
                arrivedCount++;
                queueList.add(responseDTO);
            } else if (st == BookingStatus.PROCESSING) {
                processingCount++;
                currentProcessingDTO = responseDTO;
            } else if (st == BookingStatus.COMPLETED) {
                completedCount++;
            } else if (st == BookingStatus.CANCELLED) {
                cancelledCount++;
            } else if (st == BookingStatus.CANCEL_REQUESTED) {
                pendingApprovalsCount++;
                cancellationRequests.add(responseDTO);
                pendingApprovals.add(responseDTO);
            } else if (st == BookingStatus.RESCHEDULE_REQUESTED) {
                reschedulingCount++;
                pendingApprovalsCount++;
                reschedulingRequests.add(responseDTO);
                pendingApprovals.add(responseDTO);
            }
        }

        OwnerDashboardDTO dto = new OwnerDashboardDTO();
        dto.setCentreId(centre.getCentreId());
        dto.setCentreName(centre.getName());
        dto.setCentreLocation(centre.getLocation());
        dto.setTotalCapacity(centre.getTotalCapacity());
        dto.setCurrentLoad(centre.getCurrentLoad());
        dto.setAvailableCapacity(centre.getAvailableCapacity());
        dto.setStatus(centre.getStatus());
        dto.setOverloaded(centre.getStatus() == CentreStatus.OVERLOADED || (centre.getCurrentLoad() != null && centre.getTotalCapacity() != null && centre.getCurrentLoad() >= centre.getTotalCapacity()));
        dto.setOverloadReason(dto.isOverloaded() ? "Centre capacity exceeded. Waiting list active." : "Operating normally");

        dto.setTodaysFarmersCount(todaysFarmersCount);
        dto.setWaitingCount(waitingCount);
        dto.setCalledCount(calledCount);
        dto.setArrivedCount(arrivedCount);
        dto.setProcessingCount(processingCount);
        dto.setCompletedCount(completedCount);
        dto.setCancelledCount(cancelledCount);
        dto.setReschedulingCount(reschedulingCount);
        dto.setPendingApprovalsCount(pendingApprovalsCount);

        dto.setCurrentProcessing(currentProcessingDTO);
        dto.setQueueList(queueList);
        dto.setTodaysFarmers(todaysFarmers);
        dto.setCancellationRequests(cancellationRequests);
        dto.setReschedulingRequests(reschedulingRequests);
        dto.setPendingApprovals(pendingApprovals);

        return dto;
    }

    public List<BookingResponse> getQueue(String phone) {
        ProcurementCentre centre = getOwnerCentre(phone);
        List<Booking> allBookings = bookingRepository.findByCentreCentreId(centre.getCentreId());
        List<BookingResponse> queue = new ArrayList<>();
        for (Booking b : allBookings) {
            BookingStatus st = b.getStatus();
            if (st == BookingStatus.WAITING || st == BookingStatus.CONFIRMED || st == BookingStatus.BOOKED || st == BookingStatus.CALLED || st == BookingStatus.ARRIVED) {
                queue.add(mapToResponse(b));
            }
        }
        return queue;
    }

    @Transactional
    public BookingResponse callNextFarmer(String phone) {
        ProcurementCentre centre = getOwnerCentre(phone);
        List<Booking> allBookings = bookingRepository.findByCentreCentreId(centre.getCentreId());

        Booking nextBooking = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.WAITING || b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.BOOKED)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("No waiting farmers in queue to call"));

        nextBooking.setStatus(BookingStatus.CALLED);
        nextBooking.setCalledAt(LocalDateTime.now());
        Booking saved = bookingRepository.save(nextBooking);

        return mapToResponse(saved);
    }

    @Transactional
    public BookingResponse markArrived(String phone, Long bookingId) {
        ProcurementCentre centre = getOwnerCentre(phone);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getCentre().getCentreId().equals(centre.getCentreId())) {
            throw new BadRequestException("Unauthorized: Booking belongs to another procurement centre");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Invalid transition: Booking is already COMPLETED");
        }

        booking.setStatus(BookingStatus.ARRIVED);
        booking.setArrivalTime(LocalDateTime.now());
        Booking saved = bookingRepository.save(booking);

        return mapToResponse(saved);
    }

    @Transactional
    public BookingResponse startProcurement(String phone, Long bookingId) {
        ProcurementCentre centre = getOwnerCentre(phone);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getCentre().getCentreId().equals(centre.getCentreId())) {
            throw new BadRequestException("Unauthorized: Booking belongs to another procurement centre");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Invalid transition: Booking is already COMPLETED");
        }

        booking.setStatus(BookingStatus.PROCESSING);
        booking.setProcessingStartTime(LocalDateTime.now());
        Booking saved = bookingRepository.save(booking);

        return mapToResponse(saved);
    }

    @Transactional
    public BookingResponse completeProcurement(String phone, Long bookingId) {
        ProcurementCentre centre = getOwnerCentre(phone);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getCentre().getCentreId().equals(centre.getCentreId())) {
            throw new BadRequestException("Unauthorized: Booking belongs to another procurement centre");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Procurement is already COMPLETED for booking #" + bookingId);
        }

        // STEP 6 — COMPLETE PROCUREMENT DATABASE TRANSACTION
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setCompletionTime(LocalDateTime.now());

        double current = centre.getCurrentLoad() != null ? centre.getCurrentLoad() : 0.0;
        double quantity = booking.getQuantity() != null ? booking.getQuantity() : 0.0;
        double newLoad = Math.max(0.0, current - quantity);
        centre.setCurrentLoad(newLoad);

        if (centre.getTotalCapacity() != null && newLoad < centre.getTotalCapacity() && centre.getStatus() == CentreStatus.OVERLOADED) {
            centre.setStatus(CentreStatus.ACTIVE);
        }
        centreRepository.save(centre);

        Booking saved = bookingRepository.save(booking);
        return mapToResponse(saved);
    }

    @Transactional
    public BookingResponse skipFarmer(String phone, Long bookingId, String reason) {
        ProcurementCentre centre = getOwnerCentre(phone);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getCentre().getCentreId().equals(centre.getCentreId())) {
            throw new BadRequestException("Unauthorized: Booking belongs to another procurement centre");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Cannot skip a completed procurement");
        }

        booking.setStatus(BookingStatus.SKIPPED);
        booking.setSkippedAt(LocalDateTime.now());
        booking.setSkipReason(reason != null ? reason : "Skipped by centre operator");
        Booking saved = bookingRepository.save(booking);

        return mapToResponse(saved);
    }

    public List<BookingResponse> getCancellationRequests(String phone) {
        ProcurementCentre centre = getOwnerCentre(phone);
        List<Booking> allBookings = bookingRepository.findByCentreCentreId(centre.getCentreId());
        List<BookingResponse> list = new ArrayList<>();
        for (Booking b : allBookings) {
            if (b.getStatus() == BookingStatus.CANCEL_REQUESTED) {
                list.add(mapToResponse(b));
            }
        }
        return list;
    }

    @Transactional
    public BookingResponse approveCancellation(String phone, Long bookingId) {
        ProcurementCentre centre = getOwnerCentre(phone);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getCentre().getCentreId().equals(centre.getCentreId())) {
            throw new BadRequestException("Unauthorized: Booking belongs to another procurement centre");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        double current = centre.getCurrentLoad() != null ? centre.getCurrentLoad() : 0.0;
        double quantity = booking.getQuantity() != null ? booking.getQuantity() : 0.0;
        double updatedLoad = Math.max(0.0, current - quantity);
        centre.setCurrentLoad(updatedLoad);
        if (centre.getTotalCapacity() != null && updatedLoad < centre.getTotalCapacity() && centre.getStatus() == CentreStatus.OVERLOADED) {
            centre.setStatus(CentreStatus.ACTIVE);
        }
        centreRepository.save(centre);

        Booking saved = bookingRepository.save(booking);

        // TRIGGER EVENT: OWNER_APPROVAL
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("action", "approved");
            params.put("type", "Cancellation");
            notificationService.sendNotification(saved.getFarmer().getFarmerId(), NotificationEventType.OWNER_APPROVAL, params, "owner_appr_cancel_" + bookingId);
        } catch (Exception ignored) {
        }

        return mapToResponse(saved);
    }

    @Transactional
    public BookingResponse rejectCancellation(String phone, Long bookingId) {
        ProcurementCentre centre = getOwnerCentre(phone);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getCentre().getCentreId().equals(centre.getCentreId())) {
            throw new BadRequestException("Unauthorized: Booking belongs to another procurement centre");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(booking);

        // TRIGGER EVENT: OWNER_APPROVAL
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("action", "rejected");
            params.put("type", "Cancellation");
            notificationService.sendNotification(saved.getFarmer().getFarmerId(), NotificationEventType.OWNER_APPROVAL, params, "owner_rej_cancel_" + bookingId);
        } catch (Exception ignored) {
        }

        return mapToResponse(saved);
    }

    public List<BookingResponse> getReschedulingRequests(String phone) {
        ProcurementCentre centre = getOwnerCentre(phone);
        List<Booking> allBookings = bookingRepository.findByCentreCentreId(centre.getCentreId());
        List<BookingResponse> list = new ArrayList<>();
        for (Booking b : allBookings) {
            if (b.getStatus() == BookingStatus.RESCHEDULE_REQUESTED) {
                list.add(mapToResponse(b));
            }
        }
        return list;
    }

    @Transactional
    public BookingResponse approveRescheduling(String phone, Long bookingId, AssignSlotRequest request) {
        ProcurementCentre centre = getOwnerCentre(phone);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getCentre().getCentreId().equals(centre.getCentreId())) {
            throw new BadRequestException("Unauthorized: Booking belongs to another procurement centre");
        }

        if (request != null && request.getNewDate() != null) {
            booking.setBookingDate(request.getNewDate());
        }
        if (request != null && request.getNewSlot() != null) {
            booking.setSlot(request.getNewSlot());
        }

        booking.setStatus(BookingStatus.RESCHEDULED);
        Booking saved = bookingRepository.save(booking);

        // TRIGGER EVENT: OWNER_APPROVAL
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("action", "approved");
            params.put("type", "Rescheduling");
            params.put("slot", saved.getSlot());
            params.put("bookingDate", saved.getBookingDate() != null ? saved.getBookingDate().toString() : "");
            notificationService.sendNotification(saved.getFarmer().getFarmerId(), NotificationEventType.OWNER_APPROVAL, params, "owner_appr_resched_" + bookingId);
        } catch (Exception ignored) {
        }

        return mapToResponse(saved);
    }

    @Transactional
    public BookingResponse rejectRescheduling(String phone, Long bookingId) {
        ProcurementCentre centre = getOwnerCentre(phone);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getCentre().getCentreId().equals(centre.getCentreId())) {
            throw new BadRequestException("Unauthorized: Booking belongs to another procurement centre");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(booking);

        // TRIGGER EVENT: OWNER_APPROVAL
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("action", "rejected");
            params.put("type", "Rescheduling");
            notificationService.sendNotification(saved.getFarmer().getFarmerId(), NotificationEventType.OWNER_APPROVAL, params, "owner_rej_resched_" + bookingId);
        } catch (Exception ignored) {
        }

        return mapToResponse(saved);
    }

    @Transactional
    public void changeCentreAndNotify(Long bookingId, Long newCentreId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        ProcurementCentre oldCentre = booking.getCentre();
        ProcurementCentre newCentre = centreRepository.findById(newCentreId)
                .orElseThrow(() -> new ResourceNotFoundException("Centre not found with id: " + newCentreId));

        if (oldCentre != null && oldCentre.getCentreId().equals(newCentre.getCentreId())) {
            // CRITICAL BUSINESS RULE: Centre A -> Centre A = do not notify!
            return;
        }

        String oldCentreName = oldCentre != null ? oldCentre.getName() : "Centre A";
        String newCentreName = newCentre.getName();

        booking.setCentre(newCentre);
        bookingRepository.save(booking);

        Map<String, Object> params = new HashMap<>();
        params.put("oldCentre", oldCentreName);
        params.put("newCentre", newCentreName);
        notificationService.sendNotification(
                booking.getFarmer().getFarmerId(),
                NotificationEventType.CENTRE_CHANGED,
                params,
                "centre_change_booking_" + bookingId + "_from_" + (oldCentre != null ? oldCentre.getCentreId() : 0) + "_to_" + newCentreId
        );
    }

    @Transactional
    public void completePaymentAndNotify(Long bookingId, double amount, String paymentRef) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("paymentRef", paymentRef != null ? paymentRef : ("TXN-" + bookingId));
        notificationService.sendNotification(
                booking.getFarmer().getFarmerId(),
                NotificationEventType.PAYMENT_COMPLETED,
                params,
                "payment_comp_booking_" + bookingId
        );
    }

    private BookingResponse mapToResponse(Booking booking) {
        Farmer f = booking.getFarmer();
        ProcurementCentre c = booking.getCentre();
        Crop cr = booking.getCrop();

        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .farmerId(f != null ? f.getFarmerId() : null)
                .farmerName(f != null && f.getName() != null ? f.getName() : "Farmer Ramesh")
                .centreId(c != null ? c.getCentreId() : null)
                .centreName(c != null && c.getName() != null ? c.getName() : "Centre A")
                .cropId(cr != null ? cr.getCropId() : null)
                .cropType(cr != null && cr.getCropType() != null ? cr.getCropType() : "Paddy")
                .quantity(booking.getQuantity())
                .bookingDate(booking.getBookingDate())
                .slot(booking.getSlot())
                .status(booking.getStatus())
                .availableCapacity(c != null ? c.getAvailableCapacity() : 0.0)
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
