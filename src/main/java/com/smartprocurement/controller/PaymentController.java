package com.smartprocurement.controller;

import com.smartprocurement.dto.OwnerPaymentOverviewDTO;
import com.smartprocurement.dto.PaymentDTO;
import com.smartprocurement.entity.PaymentMethod;
import com.smartprocurement.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/initiate/{procurementId}")
    public ResponseEntity<PaymentDTO> initiatePayment(@PathVariable Long procurementId, @RequestBody(required = false) Map<String, String> body) {
        String methodStr = body != null ? body.get("paymentMethod") : null;
        PaymentMethod method = PaymentMethod.DIRECT_BENEFIT_TRANSFER;
        if (methodStr != null) {
            try {
                method = PaymentMethod.valueOf(methodStr.toUpperCase());
            } catch (Exception ignored) {}
        }
        PaymentDTO dto = paymentService.initiatePayment(procurementId, method);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{paymentId}/simulate-success")
    public ResponseEntity<PaymentDTO> simulatePaymentSuccess(@PathVariable Long paymentId) {
        PaymentDTO dto = paymentService.simulatePaymentSuccess(paymentId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{paymentId}/simulate-failure")
    public ResponseEntity<PaymentDTO> simulatePaymentFailure(@PathVariable Long paymentId, @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : "Bank network timeout during DBT processing.";
        PaymentDTO dto = paymentService.simulatePaymentFailure(paymentId, reason);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{paymentId}/retry")
    public ResponseEntity<PaymentDTO> retryPayment(@PathVariable Long paymentId) {
        PaymentDTO dto = paymentService.retryPayment(paymentId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<PaymentDTO>> getFarmerPaymentHistory(@PathVariable Long farmerId) {
        List<PaymentDTO> dtos = paymentService.getFarmerPaymentHistory(farmerId);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/owner/overview")
    public ResponseEntity<OwnerPaymentOverviewDTO> getOwnerPaymentOverview(@RequestParam(required = false, defaultValue = "1") Long centreId) {
        OwnerPaymentOverviewDTO dto = paymentService.getOwnerPaymentOverview(centreId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long paymentId) {
        PaymentDTO dto = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(dto);
    }
}
