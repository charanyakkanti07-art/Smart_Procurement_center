package com.smartprocurement.controller;

import com.smartprocurement.dto.ActionRequiredDTO;
import com.smartprocurement.dto.ApprovalRequestDTO;
import com.smartprocurement.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approval-requests")
@CrossOrigin(origins = "*")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    private String getOwnerPhone(Authentication auth, String phone) {
        if (auth != null && auth.getName() != null && !auth.getName().equals("anonymousUser")) {
            return auth.getName();
        }
        return phone != null ? phone : "9876543210";
    }

    @GetMapping
    public ResponseEntity<List<ApprovalRequestDTO>> getPendingApprovals(
            Authentication auth,
            @RequestParam(value = "phone", required = false) String phone) {
        String ownerPhone = getOwnerPhone(auth, phone);
        List<ApprovalRequestDTO> list = approvalService.getPendingApprovals(ownerPhone);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApprovalRequestDTO> getApprovalById(@PathVariable("id") Long id) {
        ApprovalRequestDTO dto = approvalService.getApprovalRequestById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApprovalRequestDTO> approveRequest(
            Authentication auth,
            @PathVariable("id") Long id,
            @RequestParam(value = "phone", required = false) String phone) {
        String ownerPhone = getOwnerPhone(auth, phone);
        ApprovalRequestDTO dto = approvalService.approveRequest(id, ownerPhone);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApprovalRequestDTO> rejectRequest(
            Authentication auth,
            @PathVariable("id") Long id,
            @RequestParam(value = "phone", required = false) String phone) {
        String ownerPhone = getOwnerPhone(auth, phone);
        ApprovalRequestDTO dto = approvalService.rejectRequest(id, ownerPhone);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/action-required")
    public ResponseEntity<List<ActionRequiredDTO>> getActionRequiredAlerts(
            Authentication auth,
            @RequestParam(value = "phone", required = false) String phone) {
        String ownerPhone = getOwnerPhone(auth, phone);
        List<ActionRequiredDTO> alerts = approvalService.getActionRequiredAlerts(ownerPhone);
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/action-required/{bookingId}")
    public ResponseEntity<Map<String, Object>> resolveActionRequired(
            Authentication auth,
            @PathVariable("bookingId") Long bookingId,
            @RequestParam("action") String action,
            @RequestBody(required = false) Map<String, Object> body,
            @RequestParam(value = "phone", required = false) String phone) {
        String ownerPhone = getOwnerPhone(auth, phone);
        approvalService.resolveActionRequired(bookingId, action, ownerPhone, body);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Action Required resolved with action: " + action);
        return ResponseEntity.ok(response);
    }
}
