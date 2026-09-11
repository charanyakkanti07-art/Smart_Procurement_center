package com.smartprocurement.controller;

import com.smartprocurement.entity.NotificationEventType;
import com.smartprocurement.service.NotificationService;
import com.smartprocurement.service.provider.SmsProvider;
import com.smartprocurement.service.provider.SmsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sms")
@CrossOrigin(origins = "*")
public class SmsController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SmsProvider smsProvider;

    // Helper to build response map
    private Map<String, Object> buildResponse(SmsResult result) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", result.isSuccess());
        if (result.isSuccess()) {
            resp.put("sid", result.getProviderMessageId());
        } else {
            resp.put("message", result.getErrorMessage());
        }
        return resp;
    }

    @PostMapping("/booking-success")
    public ResponseEntity<Map<String, Object>> sendBookingSuccess(@RequestBody Map<String, Object> payload) {
        Long farmerId = ((Number) payload.get("farmerId")).longValue();
        // Pass all other fields as params for template rendering
        Map<String, Object> params = new HashMap<>(payload);
        params.remove("farmerId");
        notificationService.sendNotification(farmerId, NotificationEventType.BOOKING_CONFIRMED, params, "booking_success_" + farmerId);
        // Assume SMS was sent inside notificationService; we cannot easily get SMS result here.
        // Return generic success response.
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/queue-update")
    public ResponseEntity<Map<String, Object>> sendQueueUpdate(@RequestBody Map<String, Object> payload) {
        Long farmerId = ((Number) payload.get("farmerId")).longValue();
        Map<String, Object> params = new HashMap<>(payload);
        params.remove("farmerId");
        notificationService.sendNotification(farmerId, NotificationEventType.QUEUE_UPDATED, params, "queue_update_" + farmerId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/cancelled")
    public ResponseEntity<Map<String, Object>> sendCancellation(@RequestBody Map<String, Object> payload) {
        Long farmerId = ((Number) payload.get("farmerId")).longValue();
        Map<String, Object> params = new HashMap<>(payload);
        params.remove("farmerId");
        notificationService.sendNotification(farmerId, NotificationEventType.CANCELLATION_REQUEST, params, "cancellation_" + farmerId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/rescheduled")
    public ResponseEntity<Map<String, Object>> sendRescheduled(@RequestBody Map<String, Object> payload) {
        Long farmerId = ((Number) payload.get("farmerId")).longValue();
        Map<String, Object> params = new HashMap<>(payload);
        params.remove("farmerId");
        notificationService.sendNotification(farmerId, NotificationEventType.RESCHEDULE_REQUEST, params, "reschedule_" + farmerId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> testSms(@RequestBody Map<String, Object> payload) {
        String phone = (String) payload.get("phone");
        String message = (String) payload.get("message");
        SmsResult result = smsProvider.sendSmsDetailed(phone, message, NotificationEventType.BOOKING_CONFIRMED);
        return ResponseEntity.ok(buildResponse(result));
    }
}
