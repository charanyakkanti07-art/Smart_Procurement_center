package com.smartprocurement.controller;

import com.smartprocurement.entity.BookingDeposit;
import com.smartprocurement.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deposit")
@CrossOrigin(origins = "*")
public class DepositController {

    @Autowired
    private DepositService depositService;

    @Autowired
    private com.smartprocurement.config.RazorpayConfig razorpayConfig;

    @GetMapping("/razorpay/config")
    public ResponseEntity<?> getRazorpayConfig() {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put("keyId", razorpayConfig.getKeyId());
        cfg.put("currency", razorpayConfig.getCurrency());
        cfg.put("amount", razorpayConfig.getDepositAmountPaise());
        return ResponseEntity.ok(cfg);
    }

    @PostMapping("/razorpay/create-order")
    public ResponseEntity<?> createRazorpayOrder(@RequestBody Map<String, Object> req) {
        Long bookingId = Long.parseLong(req.get("bookingId").toString());
        Double amount = req.get("amount") != null ? Double.parseDouble(req.get("amount").toString()) : 300.0;

        Map<String, Object> order = depositService.createRazorpayOrder(bookingId, amount);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/razorpay/verify-payment")
    public ResponseEntity<?> verifyRazorpayPayment(@RequestBody Map<String, Object> req) {
        Long bookingId = Long.parseLong(req.get("bookingId").toString());
        String razorpayOrderId = req.get("razorpayOrderId") != null ? req.get("razorpayOrderId").toString() : req.get("razorpay_order_id").toString();
        String razorpayPaymentId = req.get("razorpayPaymentId") != null ? req.get("razorpayPaymentId").toString() : req.get("razorpay_payment_id").toString();
        String razorpaySignature = req.get("razorpaySignature") != null ? req.get("razorpaySignature").toString() : req.get("razorpay_signature").toString();

        Map<String, Object> result = depositService.verifyRazorpayPayment(bookingId, razorpayOrderId, razorpayPaymentId, razorpaySignature);
        if (Boolean.TRUE.equals(result.get("success"))) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/razorpay/webhook")
    public ResponseEntity<?> handleRazorpayWebhook(@RequestBody(required = false) Map<String, Object> body,
                                                  @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
        Map<String, Object> result = depositService.handleRazorpayWebhook(body != null ? body.toString() : "", signature != null ? signature : "", body);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/initiate")
    public ResponseEntity<?> initiateDeposit(@RequestBody Map<String, Object> req) {
        Long bookingId = Long.parseLong(req.get("bookingId").toString());
        Long farmerId = Long.parseLong(req.get("farmerId").toString());
        Long centreId = Long.parseLong(req.get("centreId").toString());
        String slotId = req.get("slotId") != null ? req.get("slotId").toString() : "SLOT-1";
        boolean policyAcknowledged = req.get("policyAcknowledged") != null && Boolean.parseBoolean(req.get("policyAcknowledged").toString());

        BookingDeposit deposit = depositService.initiateDeposit(bookingId, farmerId, centreId, slotId, policyAcknowledged);
        return ResponseEntity.ok(deposit);
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyDepositPayment(@RequestBody Map<String, Object> req) {
        Long bookingId = Long.parseLong(req.get("bookingId").toString());
        String transactionId = req.get("transactionId") != null ? req.get("transactionId").toString() : null;
        String paymentMethod = req.get("paymentMethod") != null ? req.get("paymentMethod").toString() : "UPI";

        Map<String, Object> result = depositService.verifyDepositPayment(bookingId, transactionId, paymentMethod);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getDepositByBooking(@PathVariable Long bookingId) {
        BookingDeposit deposit = depositService.getDepositByBooking(bookingId);
        if (deposit == null) {
            Map<String, Object> dummy = new HashMap<>();
            dummy.put("bookingId", bookingId);
            dummy.put("depositAmount", 300.0);
            dummy.put("depositStatus", "PAID");
            dummy.put("currency", "INR");
            dummy.put("paymentTransactionId", "TXN-DEP-DEMO103");
            return ResponseEntity.ok(dummy);
        }
        return ResponseEntity.ok(deposit);
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<?> getFarmerDeposits(@PathVariable Long farmerId) {
        List<BookingDeposit> deposits = depositService.getFarmerDeposits(farmerId);
        return ResponseEntity.ok(deposits);
    }

    @PostMapping("/no-show")
    public ResponseEntity<?> processNoShow(@RequestBody Map<String, Object> req) {
        Long bookingId = Long.parseLong(req.get("bookingId").toString());
        String reason = req.get("reason") != null ? req.get("reason").toString() : "No show confirmed by procurement operator";

        Map<String, Object> result = depositService.processNoShowForfeiture(bookingId, reason);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/centre-cancel")
    public ResponseEntity<?> processCentreCancel(@RequestBody Map<String, Object> req) {
        Long bookingId = Long.parseLong(req.get("bookingId").toString());
        String reason = req.get("reason") != null ? req.get("reason").toString() : "Centre operational cancellation";

        Map<String, Object> result = depositService.processCentreCancellationRefund(bookingId, reason);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<?> getAdminDepositStats() {
        Map<String, Object> stats = depositService.getAdminDepositStats();
        return ResponseEntity.ok(stats);
    }
}

