package com.smartprocurement.controller;

import com.smartprocurement.dto.AdminDTO;
import com.smartprocurement.entity.AuditLog;
import com.smartprocurement.repository.AuditLogRepository;
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

    @PostMapping("/emergency/close")
    public ResponseEntity<List<AdminDTO.AlternativeCentreRank>> closeCentre(
            @RequestBody AdminDTO.EmergencyClosureRequest request) {
        List<AdminDTO.AlternativeCentreRank> alternatives = emergencyManagementService.triggerEmergencyClosure(
                request.getCentreId(), request.getReason());
        
        // Log emergency action
        auditLogRepository.save(AuditLog.builder()
                .centreId(request.getCentreId())
                .eventType("EMERGENCY_CENTRE_CLOSURE")
                .actor("DISTRICT_ADMIN")
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
                .actor("DISTRICT_ADMIN")
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
