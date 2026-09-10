package com.smartprocurement.controller;

import com.smartprocurement.dto.QueueStatusDTO;
import com.smartprocurement.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/queue")
@CrossOrigin(origins = "*")
public class QueueController {

    @Autowired
    private QueueService queueService;

    private String getAuthenticatedPhone(Authentication authentication, String fallbackPhone) {
        if (authentication != null && authentication.getName() != null && !authentication.getName().equals("anonymousUser")) {
            return authentication.getName();
        }
        return fallbackPhone != null ? fallbackPhone : "9876543210";
    }

    @GetMapping("/centre/{centreId}")
    public ResponseEntity<List<QueueStatusDTO>> getCentreQueue(@PathVariable Long centreId) {
        List<QueueStatusDTO> queue = queueService.getQueue(centreId);
        return ResponseEntity.ok(queue);
    }

    @GetMapping("/farmer/{bookingId}")
    public ResponseEntity<QueueStatusDTO> getFarmerQueueStatus(@PathVariable Long bookingId) {
        QueueStatusDTO status = queueService.getFarmerQueueStatus(bookingId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/call-next")
    public ResponseEntity<QueueStatusDTO> callNextFarmer(Authentication authentication,
                                                         @RequestParam(value = "centreId", required = false, defaultValue = "1") Long centreId,
                                                         @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        QueueStatusDTO status = queueService.callNextFarmer(centreId, userPhone);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/{bookingId}/start")
    public ResponseEntity<QueueStatusDTO> startProcurement(Authentication authentication,
                                                           @PathVariable Long bookingId,
                                                           @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        QueueStatusDTO status = queueService.startProcurement(bookingId, userPhone);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/{bookingId}/complete")
    public ResponseEntity<QueueStatusDTO> completeProcurement(Authentication authentication,
                                                              @PathVariable Long bookingId,
                                                              @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        QueueStatusDTO status = queueService.completeProcurement(bookingId, userPhone);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/{bookingId}/request-cancel")
    public ResponseEntity<QueueStatusDTO> requestCancellation(@PathVariable Long bookingId) {
        QueueStatusDTO status = queueService.requestCancellation(bookingId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/{bookingId}/approve-cancel")
    public ResponseEntity<QueueStatusDTO> approveCancellation(Authentication authentication,
                                                               @PathVariable Long bookingId,
                                                               @RequestParam(value = "phone", required = false) String phone) {
        String userPhone = getAuthenticatedPhone(authentication, phone);
        QueueStatusDTO status = queueService.approveCancellation(bookingId, userPhone);
        return ResponseEntity.ok(status);
    }
}
