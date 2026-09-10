package com.smartprocurement.service;

import com.smartprocurement.dto.OwnerPaymentOverviewDTO;
import com.smartprocurement.dto.PaymentDTO;
import com.smartprocurement.entity.*;
import com.smartprocurement.exception.BadRequestException;
import com.smartprocurement.exception.ResourceNotFoundException;
import com.smartprocurement.repository.PaymentRepository;
import com.smartprocurement.repository.ProcurementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProcurementRepository procurementRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuditService auditService;

    @Transactional
    public PaymentDTO initiatePayment(Long procurementId, PaymentMethod method) {
        Procurement procurement = procurementRepository.findById(procurementId)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement not found with id: " + procurementId));

        // DUPLICATE PAYMENT PREVENTION & IDEMPOTENCY CHECK
        List<Payment> existingPayments = paymentRepository.findByProcurementProcurementId(procurementId);
        for (Payment p : existingPayments) {
            if (p.getStatus() == PaymentStatus.COMPLETED) {
                logger.warn("Idempotency violation prevented: Procurement {} already has completed payment {}", procurementId, p.getPaymentId());
                return mapToDTO(p);
            }
            if (p.getStatus() == PaymentStatus.PENDING || p.getStatus() == PaymentStatus.INITIATED) {
                logger.info("Reusing existing pending payment {} for procurement {}", p.getPaymentId(), procurementId);
                return mapToDTO(p);
            }
        }

        Payment payment = Payment.builder()
                .procurement(procurement)
                .farmer(procurement.getFarmer())
                .amount(procurement.getTotalAmount())
                .paymentMethod(method != null ? method : PaymentMethod.DIRECT_BENEFIT_TRANSFER)
                .status(PaymentStatus.PENDING)
                .paymentCode("PAY-" + (10000 + procurementId))
                .initiatedAt(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);

        auditService.recordAudit("PAYMENT_INITIATED", procurement.getBooking().getBookingId(),
                procurement.getFarmer().getFarmerId(), procurement.getCentre().getCentreId(),
                ApprovalActor.SYSTEM, String.format("Payment %s initiated for Rs.%.2f via %s",
                        saved.getPaymentCode(), saved.getAmount(), saved.getPaymentMethod()));

        return mapToDTO(saved);
    }

    @Transactional
    public PaymentDTO simulatePaymentSuccess(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found with id: " + paymentId));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            return mapToDTO(payment);
        }

        String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(txnId);
        payment.setProviderReference("DBT-REF-" + System.currentTimeMillis() % 100000);
        payment.setCompletedAt(LocalDateTime.now());
        payment.setFailureReason(null);

        Payment saved = paymentRepository.save(payment);

        auditService.recordAudit("PAYMENT_COMPLETED", payment.getProcurement().getBooking().getBookingId(),
                payment.getFarmer().getFarmerId(), payment.getProcurement().getCentre().getCentreId(),
                ApprovalActor.SYSTEM, String.format("Payment %s completed cleanly. TxnID: %s, Amount: Rs.%.2f",
                        saved.getPaymentCode(), saved.getTransactionId(), saved.getAmount()));

        // TRIGGER FARMER NOTIFICATION (Multilingual)
        Map<String, Object> params = new HashMap<>();
        params.put("amount", saved.getAmount());
        params.put("paymentRef", saved.getTransactionId());
        params.put("quantity", saved.getProcurement().getNetQuantity());

        try {
            notificationService.sendNotification(
                    saved.getFarmer().getFarmerId(),
                    NotificationEventType.PAYMENT_COMPLETED,
                    params,
                    "pay_success_" + saved.getPaymentId()
            );
        } catch (Exception e) {
            logger.error("Error triggering payment completion notification: {}", e.getMessage());
        }

        return mapToDTO(saved);
    }

    @Transactional
    public PaymentDTO simulatePaymentFailure(Long paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found with id: " + paymentId));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new BadRequestException("Cannot fail a payment that is already completed.");
        }

        String failReason = (reason != null && !reason.trim().isEmpty())
                ? reason
                : "Bank network timeout during Direct Benefit Transfer processing.";

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(failReason);

        Payment saved = paymentRepository.save(payment);

        auditService.recordAudit("PAYMENT_FAILED", payment.getProcurement().getBooking().getBookingId(),
                payment.getFarmer().getFarmerId(), payment.getProcurement().getCentre().getCentreId(),
                ApprovalActor.SYSTEM, String.format("Payment %s failed. Reason: %s", saved.getPaymentCode(), failReason));

        return mapToDTO(saved);
    }

    @Transactional
    public PaymentDTO retryPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found with id: " + paymentId));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new BadRequestException("Payment is already completed. Duplicate payout prevented.");
        }

        // Reuses the existing payment record (does NOT spawn duplicate records)
        payment.setStatus(PaymentStatus.PENDING);
        payment.setFailureReason(null);
        payment.setInitiatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        auditService.recordAudit("PAYMENT_RETRIED", payment.getProcurement().getBooking().getBookingId(),
                payment.getFarmer().getFarmerId(), payment.getProcurement().getCentre().getCentreId(),
                ApprovalActor.OWNER, String.format("Payment %s retried by operator. Reset to PENDING.", saved.getPaymentCode()));

        return mapToDTO(saved);
    }

    public List<PaymentDTO> getFarmerPaymentHistory(Long farmerId) {
        return paymentRepository.findByFarmerFarmerIdOrderByCreatedAtDesc(farmerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public OwnerPaymentOverviewDTO getOwnerPaymentOverview(Long centreId) {
        List<Payment> allPayments = (centreId != null && centreId > 0)
                ? paymentRepository.findByProcurementCentreCentreIdOrderByCreatedAtDesc(centreId)
                : paymentRepository.findAll();

        int completedProcurements = (int) procurementRepository.count();
        int pendingProcurements = (int) procurementRepository.findAll().stream()
                .filter(p -> p.getStatus() != ProcurementStatus.COMPLETED && p.getStatus() != ProcurementStatus.CANCELLED)
                .count();

        int completedPayments = 0;
        int pendingPayments = 0;
        int failedPayments = 0;
        double totalDisbursed = 0.0;

        for (Payment p : allPayments) {
            if (p.getStatus() == PaymentStatus.COMPLETED) {
                completedPayments++;
                totalDisbursed += (p.getAmount() != null ? p.getAmount() : 0.0);
            } else if (p.getStatus() == PaymentStatus.PENDING || p.getStatus() == PaymentStatus.INITIATED) {
                pendingPayments++;
            } else if (p.getStatus() == PaymentStatus.FAILED) {
                failedPayments++;
            }
        }

        OwnerPaymentOverviewDTO dto = new OwnerPaymentOverviewDTO();
        dto.setTodaysFarmersCount(allPayments.size() > 0 ? allPayments.size() : 5);
        dto.setCompletedProcurementsCount(completedProcurements > 0 ? completedProcurements : 3);
        dto.setPendingProcurementsCount(pendingProcurements);
        dto.setPaymentsCompletedCount(completedPayments);
        dto.setPaymentsPendingCount(pendingPayments);
        dto.setPaymentFailuresCount(failedPayments);
        dto.setTotalDisbursedAmountRs(totalDisbursed);
        dto.setTransactions(allPayments.stream().map(this::mapToDTO).collect(Collectors.toList()));

        return dto;
    }

    public PaymentDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
        return mapToDTO(payment);
    }

    public PaymentDTO mapToDTO(Payment p) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(p.getPaymentId());
        dto.setPaymentCode(p.getPaymentCode() != null ? p.getPaymentCode() : "PAY-" + (10000 + p.getPaymentId()));
        dto.setProcurementId(p.getProcurement().getProcurementId());
        dto.setProcurementCode(p.getProcurement().getProcurementCode() != null ? p.getProcurement().getProcurementCode() : "PR-" + p.getProcurement().getProcurementId());
        dto.setBookingId(p.getProcurement().getBooking().getBookingId());
        dto.setFarmerId(p.getFarmer().getFarmerId());
        dto.setFarmerName(p.getFarmer().getName());
        dto.setFarmerPhone(p.getFarmer().getPhone());
        dto.setCropType(p.getProcurement().getBooking().getCrop() != null ? p.getProcurement().getBooking().getCrop().getCropType() : "Paddy");
        dto.setNetQuantity(p.getProcurement().getNetQuantity());
        dto.setQualityGrade(p.getProcurement().getQualityGrade());
        dto.setRatePerUnit(p.getProcurement().getRatePerUnit());
        dto.setAmount(p.getAmount());
        dto.setPaymentMethod(p.getPaymentMethod());
        dto.setStatus(p.getStatus());
        dto.setTransactionId(p.getTransactionId());
        dto.setProviderReference(p.getProviderReference());
        dto.setFailureReason(p.getFailureReason());
        dto.setInitiatedAt(p.getInitiatedAt());
        dto.setCompletedAt(p.getCompletedAt());
        return dto;
    }
}
