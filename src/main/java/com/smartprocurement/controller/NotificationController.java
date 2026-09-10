package com.smartprocurement.controller;

import com.smartprocurement.dto.NotificationDTO;
import com.smartprocurement.dto.NotificationPreferenceDTO;
import com.smartprocurement.entity.Farmer;
import com.smartprocurement.repository.FarmerRepository;
import com.smartprocurement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FarmerRepository farmerRepository;

    private Long resolveFarmerId(Long explicitFarmerId) {
        if (explicitFarmerId != null) {
            return explicitFarmerId;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String phone = authentication.getName();
            Farmer farmer = farmerRepository.findByPhone(phone).orElse(null);
            if (farmer != null) {
                return farmer.getFarmerId();
            }
        }

        // Fallback default farmer ID for demo / unauthenticated sessions
        return 1L;
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @RequestParam(value = "farmerId", required = false) Long farmerId) {
        Long resolvedId = resolveFarmerId(farmerId);
        List<NotificationDTO> notifications = notificationService.getFarmerNotifications(resolvedId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(
            @RequestParam(value = "farmerId", required = false) Long farmerId) {
        Long resolvedId = resolveFarmerId(farmerId);
        long count = notificationService.getUnreadCount(resolvedId);
        Map<String, Object> response = new HashMap<>();
        response.put("farmerId", resolvedId);
        response.put("unreadCount", count);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(
            @PathVariable("id") Long id,
            @RequestParam(value = "farmerId", required = false) Long farmerId) {
        Long resolvedId = resolveFarmerId(farmerId);
        NotificationDTO updated = notificationService.markAsRead(id, resolvedId);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/notifications/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(
            @RequestParam(value = "farmerId", required = false) Long farmerId) {
        Long resolvedId = resolveFarmerId(farmerId);
        notificationService.markAllAsRead(resolvedId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "All notifications marked as read for farmer: " + resolvedId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/notification-preferences")
    public ResponseEntity<NotificationPreferenceDTO> getPreferences(
            @RequestParam(value = "farmerId", required = false) Long farmerId) {
        Long resolvedId = resolveFarmerId(farmerId);
        NotificationPreferenceDTO preferences = notificationService.getPreferences(resolvedId);
        return ResponseEntity.ok(preferences);
    }

    @PatchMapping("/notification-preferences")
    public ResponseEntity<NotificationPreferenceDTO> updatePreferences(
            @RequestBody NotificationPreferenceDTO dto,
            @RequestParam(value = "farmerId", required = false) Long farmerId) {
        Long resolvedId = resolveFarmerId(dto.getFarmerId() != null ? dto.getFarmerId() : farmerId);
        NotificationPreferenceDTO updated = notificationService.updatePreferences(resolvedId, dto);
        return ResponseEntity.ok(updated);
    }
}
