package com.smartprocurement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "farmer_id")
    private Long farmerId;

    @Column(name = "centre_id")
    private Long centreId;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor", nullable = false)
    private ApprovalActor actor;

    @Column(length = 1000)
    private String details;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public AuditLog() {
    }

    public AuditLog(Long id, String eventType, Long bookingId, Long farmerId, Long centreId,
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

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
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

    public static AuditLogBuilder builder() {
        return new AuditLogBuilder();
    }

    public static class AuditLogBuilder {
        private Long id;
        private String eventType;
        private Long bookingId;
        private Long farmerId;
        private Long centreId;
        private ApprovalActor actor;
        private String details;
        private LocalDateTime timestamp;

        public AuditLogBuilder id(Long id) { this.id = id; return this; }
        public AuditLogBuilder eventType(String eventType) { this.eventType = eventType; return this; }
        public AuditLogBuilder bookingId(Long bookingId) { this.bookingId = bookingId; return this; }
        public AuditLogBuilder farmerId(Long farmerId) { this.farmerId = farmerId; return this; }
        public AuditLogBuilder centreId(Long centreId) { this.centreId = centreId; return this; }
        public AuditLogBuilder actor(ApprovalActor actor) { this.actor = actor; return this; }
        public AuditLogBuilder details(String details) { this.details = details; return this; }
        public AuditLogBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public AuditLog build() {
            return new AuditLog(id, eventType, bookingId, farmerId, centreId, actor, details, timestamp);
        }
    }
}
