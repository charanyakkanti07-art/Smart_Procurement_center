package com.smartprocurement.controller;

import com.smartprocurement.dto.AuditLogDTO;
import com.smartprocurement.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@CrossOrigin(origins = "*")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @GetMapping
    public ResponseEntity<List<AuditLogDTO>> getAuditLogs(@RequestParam(value = "centreId", required = false) Long centreId) {
        List<AuditLogDTO> logs = auditService.getAuditLogsForCentre(centreId);
        return ResponseEntity.ok(logs);
    }
}
