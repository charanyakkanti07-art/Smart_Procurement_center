package com.smartprocurement.controller;

import com.smartprocurement.dto.AdminDTO;
import com.smartprocurement.entity.ApprovalActor;
import com.smartprocurement.entity.AuditLog;
import com.smartprocurement.entity.Role;
import com.smartprocurement.entity.User;
import com.smartprocurement.entity.UserStatus;
import com.smartprocurement.exception.BadRequestException;
import com.smartprocurement.repository.AuditLogRepository;
import com.smartprocurement.repository.UserRepository;
import com.smartprocurement.service.AiPredictionService;
import com.smartprocurement.service.DistrictAnalyticsService;
import com.smartprocurement.service.EmergencyManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final DistrictAnalyticsService districtAnalyticsService;
    private final AiPredictionService aiPredictionService;
    private final EmergencyManagementService emergencyManagementService;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @GetMapping("/overview")
    public ResponseEntity<AdminDTO.DistrictOverview> getOverview(
            @RequestParam(defaultValue = "TODAY") String dateFilter) {
        return ResponseEntity.ok(districtAnalyticsService.getDistrictOverview(dateFilter));
    }

    @GetMapping("/centres/health")
    public ResponseEntity<List<AdminDTO.CentreHealthInfo>> getCentreHealth() {
        return ResponseEntity.ok(districtAnalyticsService.getCentreHealthList());
    }

    @GetMapping("/analytics/queue")
    public ResponseEntity<AdminDTO.QueueAnalyticsInfo> getQueueAnalytics() {
        return ResponseEntity.ok(districtAnalyticsService.getQueueAnalytics());
    }

    @GetMapping("/analytics/procurement")
    public ResponseEntity<AdminDTO.ProcurementAnalyticsInfo> getProcurementAnalytics() {
        return ResponseEntity.ok(districtAnalyticsService.getProcurementAnalytics());
    }

    @GetMapping("/ai/insights")
    public ResponseEntity<AdminDTO.AiInsightsInfo> getAiInsights() {
        return ResponseEntity.ok(aiPredictionService.getAiInsights());
    }

    @GetMapping("/owners/pending")
    public ResponseEntity<List<User>> getPendingOwners() {
        return ResponseEntity.ok(userRepository.findByRoleAndStatus(Role.OWNER, UserStatus.PENDING));
    }

    @GetMapping("/owners/all")
    public ResponseEntity<List<User>> getAllOwners() {
        return ResponseEntity.ok(userRepository.findByRole(Role.OWNER));
    }

    @PostMapping("/owners/{userId}/approve")
    public ResponseEntity<User> approveOwner(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Owner user not found: " + userId));
        
        user.setStatus(UserStatus.ACTIVE);
        User updated = userRepository.save(user);

        auditLogRepository.save(AuditLog.builder()
                .centreId(user.getCentre() != null ? user.getCentre().getCentreId() : 1L)
                .eventType("OWNER_REGISTRATION_APPROVED")
                .actor(ApprovalActor.DISTRICT_ADMIN)
                .details("Approved Mandi Owner registration for: " + user.getName() + " (Phone: " + user.getPhone() + ")")
                .timestamp(LocalDateTime.now())
                .build());

        return ResponseEntity.ok(updated);
    }

    @PostMapping("/owners/{userId}/reject")
    public ResponseEntity<User> rejectOwner(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Owner user not found: " + userId));
        
        user.setStatus(UserStatus.REJECTED);
        User updated = userRepository.save(user);

        auditLogRepository.save(AuditLog.builder()
                .centreId(user.getCentre() != null ? user.getCentre().getCentreId() : 1L)
                .eventType("OWNER_REGISTRATION_REJECTED")
                .actor(ApprovalActor.DISTRICT_ADMIN)
                .details("Rejected Mandi Owner registration for: " + user.getName() + " (Phone: " + user.getPhone() + ")")
                .timestamp(LocalDateTime.now())
                .build());

        return ResponseEntity.ok(updated);
    }

    @PostMapping("/emergency/close")
    public ResponseEntity<List<AdminDTO.AlternativeCentreRank>> closeCentre(
            @RequestBody AdminDTO.EmergencyClosureRequest request) {
        List<AdminDTO.AlternativeCentreRank> alternatives = emergencyManagementService.triggerEmergencyClosure(
                request.getCentreId(), request.getReason());
        
        auditLogRepository.save(AuditLog.builder()
                .centreId(request.getCentreId())
                .eventType("EMERGENCY_CENTRE_CLOSURE")
                .actor(ApprovalActor.DISTRICT_ADMIN)
                .details("Triggered emergency closure due to: " + request.getReason() + ". Generated 2 ranked alternative options.")
                .timestamp(LocalDateTime.now())
                .build());

        return ResponseEntity.ok(alternatives);
    }

    @PostMapping("/audit-logs/record")
    public ResponseEntity<AuditLog> recordAdminDecision(
            @RequestParam String recommendation,
            @RequestParam String decision,
            @RequestParam(defaultValue = "1") Long centreId) {
        
        AuditLog log = auditLogRepository.save(AuditLog.builder()
                .centreId(centreId)
                .eventType("ADMIN_AI_DECISION")
                .actor(ApprovalActor.DISTRICT_ADMIN)
                .details("AI Recommendation: '" + recommendation + "' -> Admin Action: " + decision)
                .timestamp(LocalDateTime.now())
                .build());

        return ResponseEntity.ok(log);
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(auditLogRepository.findAllByOrderByTimestampDesc());
    }
}
