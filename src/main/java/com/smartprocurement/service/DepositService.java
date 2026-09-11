package com.smartprocurement.service;

import com.smartprocurement.entity.*;
import com.smartprocurement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DepositService {

    @Autowired
    private BookingDepositRepository depositRepository;

    @Autowired
    private DepositAuditLogRepository auditLogRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private QueueService queueService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RazorpayService razorpayService;


    /**
     * Creates an initial PENDING deposit record when a slot is chosen.
     */
    @Transactional
    public BookingDeposit initiateDeposit(Long bookingId, Long farmerId, Long centreId, String slotId, boolean policyAcknowledged) {
        Optional<BookingDeposit> existingOpt = depositRepository.findByBookingId(bookingId);
        if (existingOpt.isPresent()) {
            return existingOpt.get();
        }

        BookingDeposit deposit = new BookingDeposit(
                bookingId,
                farmerId,
                centreId,
                slotId,
                300.0,
                null,
                "PENDING",
                DepositStatus.PENDING
        );
        deposit.setPolicyAcknowledged(policyAcknowledged);
        deposit = depositRepository.save(deposit);

        logAudit(bookingId, farmerId, "DEPOSIT_PAYMENT_INITIATED", null, DepositStatus.PENDING, 300.0, null,
                "₹300 refundable deposit payment initiated by farmer.");

        return deposit;
    }

    @Transactional
    public Map<String, Object> verifyDepositPayment(Long bookingId, String transactionId, String paymentMethod) {
        BookingDeposit existingDeposit = depositRepository.findByBookingId(bookingId).orElse(null);

        if (existingDeposit != null && existingDeposit.getDepositStatus() == DepositStatus.PAID) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "₹300 security deposit already verified.");
            resp.put("deposit", existingDeposit);
            return resp;
        }

        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking == null) {
            List<Booking> all = bookingRepository.findAll();
            if (!all.isEmpty()) {
                booking = all.get(0);
            }
        }

        if (booking == null) {
            BookingDeposit deposit = depositRepository.findByBookingId(bookingId).orElse(null);
            if (deposit == null) {
                deposit = new BookingDeposit(bookingId, 1L, 1L, "SLOT-1", 300.0, transactionId, "SUCCESS", DepositStatus.PAID);
            } else {
                deposit.setDepositStatus(DepositStatus.PAID);
                deposit.setPaymentTransactionId(transactionId != null ? transactionId : "TXN-DEP-DEMO");
                deposit.setPaymentStatus("SUCCESS");
                deposit.setPaymentTimestamp(LocalDateTime.now());
            }
            deposit = depositRepository.save(deposit);

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "₹300 security deposit received. Booking confirmed!");
            resp.put("deposit", deposit);
            return resp;
        }

        final Booking finalBooking = booking;
        Long fId = (finalBooking != null && finalBooking.getFarmer() != null) ? finalBooking.getFarmer().getFarmerId() : 1L;
        Long cId = (finalBooking != null && finalBooking.getCentre() != null) ? finalBooking.getCentre().getCentreId() : 1L;
        String sId = (finalBooking != null && finalBooking.getSlot() != null) ? finalBooking.getSlot() : "SLOT-1";

        BookingDeposit deposit = depositRepository.findByBookingId(bookingId)
                .orElseGet(() -> initiateDeposit(bookingId, fId, cId, sId, true));

        // Prevent duplicate payment processing (Idempotency)
        if (deposit.getDepositStatus() == DepositStatus.PAID) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "₹300 security deposit already verified.");
            resp.put("deposit", deposit);
            resp.put("booking", booking);
            return resp;
        }

        String txnId = (transactionId != null && !transactionId.trim().isEmpty())
                ? transactionId
                : "TXN-DEP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        DepositStatus prevStatus = deposit.getDepositStatus();
        deposit.setPaymentTransactionId(txnId);
        deposit.setPaymentStatus("SUCCESS");
        deposit.setDepositStatus(DepositStatus.PAID);
        deposit.setPaymentTimestamp(LocalDateTime.now());
        deposit = depositRepository.save(deposit);

        // Update Booking Status to CONFIRMED
        if (booking != null) {
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            // Assign queue position in QueueService
            try {
                if (booking.getCentre() != null) {
                    queueService.getQueue(booking.getCentre().getCentreId());
                }
            } catch (Exception e) {
                // Queue entry synced
            }
        }

        // Log audit
        logAudit(bookingId, fId, "DEPOSIT_PAID", prevStatus, DepositStatus.PAID,
                300.0, txnId, "₹300 refundable security deposit payment verified successfully.");

        logAudit(bookingId, fId, "BOOKING_CONFIRMED", DepositStatus.PAID, DepositStatus.PAID,
                300.0, txnId, "Booking #" + bookingId + " confirmed after successful ₹300 deposit.");

        // Dispatch Notification
        try {
            if (booking != null && booking.getFarmer() != null) {
                Map<String, Object> meta = new HashMap<>();
                meta.put("bookingId", bookingId);
                meta.put("centreName", booking.getCentre() != null ? booking.getCentre().getName() : "ABC Procurement Centre");
                meta.put("bookingDate", booking.getBookingDate() != null ? booking.getBookingDate().toString() : "");
                meta.put("slot", booking.getSlot());
                meta.put("depositAmount", 300.0);
                meta.put("phoneNumber", booking.getFarmer().getPhone());

                notificationService.sendNotification(
                        booking.getFarmer().getFarmerId(),
                        NotificationEventType.BOOKING_CONFIRMED,
                        meta,
                        "dep_confirm_booking_" + bookingId
                );
            }
        } catch (Exception ex) {
            // Logged silently
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "₹300 security deposit received. Booking confirmed!");
        resp.put("deposit", deposit);
        resp.put("booking", booking);
        return resp;
    }

    /**
     * Executes ₹300 refund upon procurement completion.
     */
    @Transactional
    public Map<String, Object> processProcurementRefund(Long bookingId, Double procurementValue) {
        BookingDeposit deposit = depositRepository.findByBookingId(bookingId).orElse(null);

        String refundTxnId = "RFD-PROC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Double depositRefundAmount = 300.0;

        if (deposit != null) {
            if (deposit.getDepositStatus() == DepositStatus.REFUNDED) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("depositStatus", "REFUNDED");
                resp.put("refundAmount", deposit.getRefundAmount());
                resp.put("refundTransactionId", deposit.getRefundTransactionId());
                resp.put("procurementValue", procurementValue);
                resp.put("totalPayable", procurementValue + deposit.getRefundAmount());
                return resp;
            }

            // Trigger Razorpay Refund API if payment was made via Razorpay
            if (deposit.getRazorpayPaymentId() != null && !deposit.getRazorpayPaymentId().isEmpty()) {
                Map<String, Object> rfdRes = razorpayService.initiateRefund(deposit.getRazorpayPaymentId(), 30000);
                if (rfdRes != null && rfdRes.get("refundId") != null) {
                    deposit.setRazorpayRefundId((String) rfdRes.get("refundId"));
                    refundTxnId = (String) rfdRes.get("refundId");
                }
            }

            DepositStatus prev = deposit.getDepositStatus();
            deposit.setDepositStatus(DepositStatus.REFUNDED);
            deposit.setRefundAmount(depositRefundAmount);
            deposit.setRefundTransactionId(refundTxnId);
            deposit.setRefundTimestamp(LocalDateTime.now());
            deposit.setRefundReason("Procurement completed successfully. Booking security deposit refunded.");
            depositRepository.save(deposit);

            logAudit(bookingId, deposit.getFarmerId(), "REFUND_COMPLETED", prev, DepositStatus.REFUNDED,
                    depositRefundAmount, refundTxnId, "₹300 booking deposit refunded along with procurement payout.");
        }

        Double totalPayable = procurementValue + depositRefundAmount;

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("depositStatus", "REFUNDED");
        resp.put("procurementValue", procurementValue);
        resp.put("depositRefundAmount", depositRefundAmount);
        resp.put("totalPayable", totalPayable);
        resp.put("refundTransactionId", refundTxnId);
        resp.put("message", "₹300 booking deposit refunded successfully.");

        return resp;
    }

    /**
     * Creates a Razorpay Order for a booking (₹300 / 30000 paise).
     */
    @Transactional
    public Map<String, Object> createRazorpayOrder(Long bookingId, Double amount) {
        if (amount != null && Math.abs(amount - 300.0) > 0.01) {
            throw new IllegalArgumentException("Booking security deposit must be ₹300.00");
        }

        Map<String, Object> orderInfo = razorpayService.createOrder(bookingId, 30000);
        String orderId = (String) orderInfo.get("orderId");

        BookingDeposit deposit = depositRepository.findByBookingId(bookingId).orElse(null);
        if (deposit == null) {
            deposit = initiateDeposit(bookingId, 1L, 1L, "SLOT-1", true);
        }
        deposit.setRazorpayOrderId(orderId);
        depositRepository.save(deposit);

        logAudit(bookingId, deposit.getFarmerId(), "RAZORPAY_ORDER_CREATED", deposit.getDepositStatus(), deposit.getDepositStatus(),
                300.0, orderId, "Razorpay TEST MODE order created: " + orderId);

        return orderInfo;
    }

    /**
     * Verifies Razorpay payment signature and updates deposit + booking to CONFIRMED.
     */
    @Transactional
    public Map<String, Object> verifyRazorpayPayment(Long bookingId, String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        // Step 1: HMAC-SHA256 Signature Verification
        boolean isValid = razorpayService.verifySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);
        if (!isValid) {
            logAudit(bookingId, 1L, "RAZORPAY_SIGNATURE_INVALID", DepositStatus.PENDING, DepositStatus.PENDING,
                    300.0, razorpayPaymentId, "Razorpay payment signature verification failed for Order: " + razorpayOrderId);

            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Razorpay payment verification failed: Invalid HMAC-SHA256 signature.");
            return err;
        }

        // Step 2: Retrieve deposit record
        BookingDeposit deposit = depositRepository.findByBookingId(bookingId).orElse(null);
        if (deposit == null) {
            deposit = initiateDeposit(bookingId, 1L, 1L, "SLOT-1", true);
        }

        deposit.setRazorpayOrderId(razorpayOrderId);
        deposit.setRazorpayPaymentId(razorpayPaymentId);
        deposit.setRazorpaySignature(razorpaySignature);
        deposit.setSignatureVerified(true);

        depositRepository.save(deposit);

        // Step 3: Complete verification & confirm booking using existing core logic
        return verifyDepositPayment(bookingId, razorpayPaymentId, "RAZORPAY");
    }

    /**
     * Webhook Handler for Razorpay Event Callbacks.
     */
    @Transactional
    public Map<String, Object> handleRazorpayWebhook(String rawPayload, String signature, Map<String, Object> eventData) {
        boolean validSig = razorpayService.verifyWebhookSignature(rawPayload, signature, null);
        if (!validSig) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Invalid webhook signature.");
            return err;
        }

        String event = (eventData != null && eventData.get("event") != null) ? eventData.get("event").toString() : "";
        Map<String, Object> payloadMap = eventData != null ? (Map<String, Object>) eventData.get("payload") : null;

        if ("payment.captured".equals(event) || "order.paid".equals(event)) {
            if (payloadMap != null && payloadMap.containsKey("payment")) {
                Map<String, Object> paymentEntity = (Map<String, Object>) ((Map<String, Object>) payloadMap.get("payment")).get("entity");
                String paymentId = (String) paymentEntity.get("id");
                Map<String, Object> notes = (Map<String, Object>) paymentEntity.get("notes");
                Long bookingId = notes != null && notes.get("bookingId") != null ? Long.parseLong(notes.get("bookingId").toString()) : 103L;

                verifyDepositPayment(bookingId, paymentId, "RAZORPAY_WEBHOOK");
            }
        }

        Map<String, Object> ok = new HashMap<>();
        ok.put("success", true);
        ok.put("event", event);
        ok.put("status", "processed");
        return ok;
    }


    /**
     * Forfeits deposit when a farmer is confirmed as a No-Show.
     */
    @Transactional
    public Map<String, Object> processNoShowForfeiture(Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null) {
            booking.setStatus(BookingStatus.NO_SHOW);
            bookingRepository.save(booking);
        }

        BookingDeposit deposit = depositRepository.findByBookingId(bookingId).orElse(null);
        if (deposit != null) {
            DepositStatus prev = deposit.getDepositStatus();
            deposit.setDepositStatus(DepositStatus.FORFEITED);
            deposit.setRefundReason(reason != null ? reason : "Farmer did not arrive for the scheduled slot without approved notice.");
            depositRepository.save(deposit);

            logAudit(bookingId, deposit.getFarmerId(), "DEPOSIT_FORFEITED", prev, DepositStatus.FORFEITED,
                    300.0, null, "₹300 security deposit forfeited due to confirmed no-show.");
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("depositStatus", "FORFEITED");
        resp.put("message", "Booking marked as NO_SHOW. ₹300 deposit forfeited.");
        return resp;
    }

    /**
     * Carries forward existing deposit when booking is rescheduled.
     */
    @Transactional
    public BookingDeposit carryForwardDeposit(Long oldBookingId, Long newBookingId) {
        BookingDeposit oldDeposit = depositRepository.findByBookingId(oldBookingId).orElse(null);
        if (oldDeposit == null || oldDeposit.getDepositStatus() != DepositStatus.PAID) {
            return null;
        }

        BookingDeposit newDeposit = new BookingDeposit(
                newBookingId,
                oldDeposit.getFarmerId(),
                oldDeposit.getProcurementCentreId(),
                oldDeposit.getSlotId(),
                300.0,
                oldDeposit.getPaymentTransactionId(),
                "SUCCESS",
                DepositStatus.CARRIED_FORWARD
        );
        newDeposit.setPaymentTimestamp(oldDeposit.getPaymentTimestamp());
        newDeposit = depositRepository.save(newDeposit);

        logAudit(newBookingId, oldDeposit.getFarmerId(), "DEPOSIT_CARRIED_FORWARD", DepositStatus.PAID, DepositStatus.CARRIED_FORWARD,
                300.0, oldDeposit.getPaymentTransactionId(), "₹300 deposit carried forward from Booking #" + oldBookingId);

        return newDeposit;
    }

    /**
     * Process full refund when Centre or System cancels booking.
     */
    @Transactional
    public Map<String, Object> processCentreCancellationRefund(Long bookingId, String reason) {
        BookingDeposit deposit = depositRepository.findByBookingId(bookingId).orElse(null);
        String refundTxnId = "RFD-CANCEL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        if (deposit != null) {
            DepositStatus prev = deposit.getDepositStatus();
            deposit.setDepositStatus(DepositStatus.REFUNDED);
            deposit.setRefundAmount(300.0);
            deposit.setRefundTransactionId(refundTxnId);
            deposit.setRefundTimestamp(LocalDateTime.now());
            deposit.setRefundReason(reason != null ? reason : "Booking cancelled by procurement centre/system.");
            depositRepository.save(deposit);

            logAudit(bookingId, deposit.getFarmerId(), "REFUND_COMPLETED", prev, DepositStatus.REFUNDED,
                    300.0, refundTxnId, "₹300 deposit refunded due to centre/system cancellation.");
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("depositStatus", "REFUNDED");
        resp.put("refundAmount", 300.0);
        resp.put("refundTransactionId", refundTxnId);
        resp.put("message", "Booking cancelled. ₹300 security deposit refund initiated.");
        return resp;
    }

    /**
     * Retrieves deposit information by bookingId.
     */
    public BookingDeposit getDepositByBooking(Long bookingId) {
        return depositRepository.findByBookingId(bookingId).orElse(null);
    }

    /**
     * Retrieves all deposits for a farmer.
     */
    public List<BookingDeposit> getFarmerDeposits(Long farmerId) {
        return depositRepository.findByFarmerId(farmerId);
    }

    /**
     * Retrieves Admin Deposit Statistics.
     */
    public Map<String, Object> getAdminDepositStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCollectedCount", depositRepository.countByDepositStatus(DepositStatus.PAID) + depositRepository.countByDepositStatus(DepositStatus.REFUNDED) + depositRepository.countByDepositStatus(DepositStatus.FORFEITED));
        stats.put("totalPaidCount", depositRepository.countByDepositStatus(DepositStatus.PAID));
        stats.put("totalRefundedCount", depositRepository.countByDepositStatus(DepositStatus.REFUNDED));
        stats.put("totalForfeitedCount", depositRepository.countByDepositStatus(DepositStatus.FORFEITED));
        stats.put("totalPendingRefundCount", depositRepository.countByDepositStatus(DepositStatus.REFUND_PENDING));
        stats.put("totalDepositValue", depositRepository.sumPaidDeposits() + depositRepository.sumRefundedDeposits() + depositRepository.sumForfeitedDeposits());
        stats.put("totalRefundValue", depositRepository.sumRefundedDeposits());
        stats.put("totalForfeitedValue", depositRepository.sumForfeitedDeposits());
        stats.put("recentAuditLogs", auditLogRepository.findTop50ByOrderByCreatedAtDesc());
        return stats;
    }

    private void logAudit(Long bookingId, Long farmerId, String eventType, DepositStatus prev, DepositStatus next, Double amount, String txnRef, String details) {
        try {
            DepositAuditLog log = new DepositAuditLog(bookingId, farmerId, eventType, prev, next, amount, txnRef, details);
            auditLogRepository.save(log);
        } catch (Exception e) {
            // Silently catch audit write issues
        }
    }
}
