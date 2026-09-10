package com.smartprocurement.dto;

import com.smartprocurement.entity.ApprovalActor;

import java.time.LocalDateTime;

public class AuditLogDTO {
    private Long id;
    private String eventType;
    private Long bookingId;
    private Long farmerId;
    private Long centreId;
    private ApprovalActor actor;
    private String details;
    private LocalDateTime timestamp;

    public AuditLogDTO() {
    }

    public AuditLogDTO(Long id, String eventType, Long bookingId, Long farmerId, Long centreId,
                       ApprovalActor actor, String details, LocalDateTime timestamp) {
        this.id = id;
        this.eventType = eventType;
        this.bookingId = bookingId;
        this.farmerId = farmerId;
        this.centreId = centreId;
        this.actor = actor;
        this.details = details;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public Long getCentreId() { return centreId; }
    public void setCentreId(Long centreId) { this.centreId = centreId; }

    public ApprovalActor getActor() { return actor; }
    public void setActor(ApprovalActor actor) { this.actor = actor; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public static AuditLogDTOBuilder builder() {
        return new AuditLogDTOBuilder();
    }

    public static class AuditLogDTOBuilder {
        private Long id;
        private String eventType;
        private Long bookingId;
        private Long farmerId;
        private Long centreId;
        private ApprovalActor actor;
        private String details;
        private LocalDateTime timestamp;

        public AuditLogDTOBuilder id(Long id) { this.id = id; return this; }
        public AuditLogDTOBuilder eventType(String eventType) { this.eventType = eventType; return this; }
        public AuditLogDTOBuilder bookingId(Long bookingId) { this.bookingId = bookingId; return this; }
        public AuditLogDTOBuilder farmerId(Long farmerId) { this.farmerId = farmerId; return this; }
        public AuditLogDTOBuilder centreId(Long centreId) { this.centreId = centreId; return this; }
        public AuditLogDTOBuilder actor(ApprovalActor actor) { this.actor = actor; return this; }
        public AuditLogDTOBuilder details(String details) { this.details = details; return this; }
        public AuditLogDTOBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public AuditLogDTO build() {
            return new AuditLogDTO(id, eventType, bookingId, farmerId, centreId, actor, details, timestamp);
        }
    }
}
