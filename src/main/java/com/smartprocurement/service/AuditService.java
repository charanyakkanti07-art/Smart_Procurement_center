package com.smartprocurement.service;

import com.smartprocurement.dto.AuditLogDTO;
import com.smartprocurement.entity.ApprovalActor;
import com.smartprocurement.entity.AuditLog;
import com.smartprocurement.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Transactional
    public AuditLog recordAudit(String eventType, Long bookingId, Long farmerId, Long centreId, ApprovalActor actor, String details) {
        AuditLog log = AuditLog.builder()
                .eventType(eventType)
                .bookingId(bookingId)
                .farmerId(farmerId)
                .centreId(centreId)
                .actor(actor != null ? actor : ApprovalActor.SYSTEM)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();

        logger.info("[AUDIT LOG] Event: {}, BookingId: {}, FarmerId: {}, Actor: {}, Details: {}",
                eventType, bookingId, farmerId, actor, details);

        return auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLogDTO> getAuditLogsForCentre(Long centreId) {
        List<AuditLog> logs = centreId != null ?
                auditLogRepository.findByCentreIdOrderByTimestampDesc(centreId) :
                auditLogRepository.findAllByOrderByTimestampDesc();

        return logs.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public AuditLogDTO mapToDTO(AuditLog log) {
        return AuditLogDTO.builder()
                .id(log.getId())
                .eventType(log.getEventType())
                .bookingId(log.getBookingId())
                .farmerId(log.getFarmerId())
                .centreId(log.getCentreId())
                .actor(log.getActor())
                .details(log.getDetails())
                .timestamp(log.getTimestamp())
                .build();
    }
}
