package com.smartprocurement.service;

import com.smartprocurement.dto.ProcurementDTO;
import com.smartprocurement.dto.ProcurementRequestDTO;
import com.smartprocurement.entity.*;
import com.smartprocurement.exception.BadRequestException;
import com.smartprocurement.exception.ResourceNotFoundException;
import com.smartprocurement.repository.BookingRepository;
import com.smartprocurement.repository.ProcurementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProcurementService {

    private static final Logger logger = LoggerFactory.getLogger(ProcurementService.class);

    @Autowired
    private ProcurementRepository procurementRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ProcurementRateService rateService;

    @Autowired
    private AuditService auditService;

    @Autowired
    @Lazy
    private PaymentService paymentService;

    @Transactional
    public ProcurementDTO markArrival(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        booking.setStatus(BookingStatus.ARRIVED);
        booking.setArrivalTime(LocalDateTime.now());
        bookingRepository.save(booking);

        Procurement procurement = procurementRepository.findByBookingBookingId(bookingId)
                .orElseGet(() -> Procurement.builder()
                        .booking(booking)
                        .farmer(booking.getFarmer())
                        .centre(booking.getCentre())
                        .status(ProcurementStatus.ARRIVED)
                        .build());

        procurement.setStatus(ProcurementStatus.ARRIVED);
        Procurement saved = procurementRepository.save(procurement);

        auditService.recordAudit("FARMER_ARRIVED", bookingId, booking.getFarmer().getFarmerId(),
                booking.getCentre().getCentreId(), ApprovalActor.OWNER,
                "Farmer marked as arrived at centre: " + booking.getCentre().getName());

        return mapToDTO(saved);
    }

    @Transactional
    public ProcurementDTO verifyFarmer(Long bookingId, String operatorName) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.REJECTED) {
            throw new BadRequestException("Invalid booking. Booking is cancelled or rejected.");
        }

        Procurement procurement = procurementRepository.findByBookingBookingId(bookingId)
                .orElseGet(() -> Procurement.builder()
                        .booking(booking)
                        .farmer(booking.getFarmer())
                        .centre(booking.getCentre())
                        .status(ProcurementStatus.VERIFIED)
                        .build());

        procurement.setStatus(ProcurementStatus.VERIFIED);
        procurement.setVerifiedBy(operatorName != null ? operatorName : "Operator");
        procurement.setVerifiedAt(LocalDateTime.now());

        booking.setStatus(BookingStatus.PROCESSING);
        booking.setProcessingStartTime(LocalDateTime.now());
        bookingRepository.save(booking);

        Procurement saved = procurementRepository.save(procurement);

        auditService.recordAudit("FARMER_VERIFIED", bookingId, booking.getFarmer().getFarmerId(),
                booking.getCentre().getCentreId(), ApprovalActor.OWNER,
                "Farmer identity & booking verified by " + procurement.getVerifiedBy());

        return mapToDTO(saved);
    }

    @Transactional
    public ProcurementDTO recordWeighing(Long bookingId, ProcurementRequestDTO request) {
        Procurement procurement = procurementRepository.findByBookingBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement verification record not found for booking: " + bookingId));

        Double gross = request.getGrossWeight();
        Double tare = request.getTareWeight();

        if (gross == null || gross <= 0) {
            throw new BadRequestException("Please enter a valid gross weight greater than zero.");
        }
        if (tare == null || tare < 0 || gross <= tare) {
            throw new BadRequestException("Tare weight must be non-negative and less than gross weight.");
        }

        double net = gross - tare;
        procurement.setGrossWeight(gross);
        procurement.setTareWeight(tare);
        procurement.setNetQuantity(net);
        procurement.setUnit(request.getUnit() != null ? request.getUnit() : "kg");
        procurement.setStatus(ProcurementStatus.WEIGHING);
        procurement.setWeighedBy(request.getOperatorName() != null ? request.getOperatorName() : "Weighing Scale #1");
        procurement.setWeighedAt(LocalDateTime.now());

        Procurement saved = procurementRepository.save(procurement);

        auditService.recordAudit("WEIGHT_RECORDED", bookingId, procurement.getFarmer().getFarmerId(),
                procurement.getCentre().getCentreId(), ApprovalActor.OWNER,
                String.format("Weight recorded: Gross=%.1fkg, Tare=%.1fkg, Net=%.1fkg", gross, tare, net));

        return mapToDTO(saved);
    }

    @Transactional
    public ProcurementDTO recordQualityCheck(Long bookingId, ProcurementRequestDTO request) {
        Procurement procurement = procurementRepository.findByBookingBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement record not found for booking: " + bookingId));

        if (procurement.getNetQuantity() == null || procurement.getNetQuantity() <= 0) {
            throw new BadRequestException("Procurement cannot be completed. Final quantity has not been recorded.");
        }
        if (request.getQualityStatus() == null) {
            throw new BadRequestException("Quality check status (ACCEPTED, REJECTED, NEEDS_REVIEW) is required.");
        }

        String cropType = procurement.getBooking().getCrop() != null ? procurement.getBooking().getCrop().getCropType() : "Paddy";
        String grade = request.getQualityGrade() != null ? request.getQualityGrade() : "Grade A";
        double rate = rateService.resolveRate(cropType, grade);
        double totalAmount = procurement.getNetQuantity() * rate;

        procurement.setQualityGrade(grade);
        procurement.setQualityParameters(request.getQualityParameters() != null ? request.getQualityParameters() : "Moisture: 13.5%, Foreign Matter: <1%");
        procurement.setQualityStatus(request.getQualityStatus());
        procurement.setRatePerUnit(rate);
        procurement.setTotalAmount(totalAmount);
        procurement.setStatus(ProcurementStatus.QUALITY_CHECK);
        procurement.setQualityCheckedBy(request.getOperatorName() != null ? request.getOperatorName() : "Quality Officer");
        procurement.setQualityCheckedAt(LocalDateTime.now());

        Procurement saved = procurementRepository.save(procurement);

        auditService.recordAudit("QUALITY_CHECKED", bookingId, procurement.getFarmer().getFarmerId(),
                procurement.getCentre().getCentreId(), ApprovalActor.OWNER,
                String.format("Quality checked: Grade=%s, Rate=Rs.%.2f/kg, Total=Rs.%.2f, Status=%s",
                        grade, rate, totalAmount, request.getQualityStatus()));

        return mapToDTO(saved);
    }

    @Transactional
    public ProcurementDTO confirmProcurement(Long bookingId, String operatorName) {
        Procurement procurement = procurementRepository.findByBookingBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement record not found for booking: " + bookingId));

        if (procurement.getNetQuantity() == null || procurement.getNetQuantity() <= 0) {
            throw new BadRequestException("Procurement cannot be completed. Final quantity has not been recorded.");
        }
        if (procurement.getQualityStatus() == null) {
            throw new BadRequestException("Quality check is incomplete.");
        }
        if (procurement.getRatePerUnit() == null || procurement.getTotalAmount() == null) {
            throw new BadRequestException("Applicable procurement rate is unavailable.");
        }

        if (procurement.getProcurementCode() == null) {
            procurement.setProcurementCode("PR-" + (10000 + procurement.getProcurementId()));
        }

        procurement.setStatus(ProcurementStatus.COMPLETED);
        procurement.setCompletedAt(LocalDateTime.now());

        Booking booking = procurement.getBooking();
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setCompletionTime(LocalDateTime.now());
        bookingRepository.save(booking);

        Procurement saved = procurementRepository.save(procurement);

        auditService.recordAudit("PROCUREMENT_CONFIRMED", bookingId, procurement.getFarmer().getFarmerId(),
                procurement.getCentre().getCentreId(), ApprovalActor.OWNER,
                String.format("Procurement %s confirmed for %.1f kg (Rs.%.2f)",
                        saved.getProcurementCode(), saved.getNetQuantity(), saved.getTotalAmount()));

        // Automatically trigger payment initiation
        paymentService.initiatePayment(saved.getProcurementId(), PaymentMethod.DIRECT_BENEFIT_TRANSFER);

        return mapToDTO(saved);
    }

    public ProcurementDTO getProcurementByBookingId(Long bookingId) {
        Procurement procurement = procurementRepository.findByBookingBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement record not found for booking: " + bookingId));
        return mapToDTO(procurement);
    }

    public List<ProcurementDTO> getProcurementsByFarmerId(Long farmerId) {
        return procurementRepository.findByFarmerFarmerIdOrderByCreatedAtDesc(farmerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProcurementDTO mapToDTO(Procurement p) {
        ProcurementDTO dto = new ProcurementDTO();
        dto.setProcurementId(p.getProcurementId());
        dto.setProcurementCode(p.getProcurementCode() != null ? p.getProcurementCode() : "PR-" + (10000 + p.getProcurementId()));
        dto.setBookingId(p.getBooking().getBookingId());
        dto.setFarmerId(p.getFarmer().getFarmerId());
        dto.setFarmerName(p.getFarmer().getName());
        dto.setFarmerPhone(p.getFarmer().getPhone());
        dto.setCentreId(p.getCentre().getCentreId());
        dto.setCentreName(p.getCentre().getName());
        dto.setCropType(p.getBooking().getCrop() != null ? p.getBooking().getCrop().getCropType() : "Paddy");
        dto.setGrossWeight(p.getGrossWeight());
        dto.setTareWeight(p.getTareWeight());
        dto.setNetQuantity(p.getNetQuantity());
        dto.setUnit(p.getUnit());
        dto.setQualityGrade(p.getQualityGrade());
        dto.setQualityParameters(p.getQualityParameters());
        dto.setQualityStatus(p.getQualityStatus());
        dto.setRatePerUnit(p.getRatePerUnit());
        dto.setTotalAmount(p.getTotalAmount());
        dto.setStatus(p.getStatus());
        dto.setVerifiedBy(p.getVerifiedBy());
        dto.setVerifiedAt(p.getVerifiedAt());
        dto.setWeighedBy(p.getWeighedBy());
        dto.setWeighedAt(p.getWeighedAt());
        dto.setQualityCheckedBy(p.getQualityCheckedBy());
        dto.setQualityCheckedAt(p.getQualityCheckedAt());
        dto.setCompletedAt(p.getCompletedAt());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }
}
