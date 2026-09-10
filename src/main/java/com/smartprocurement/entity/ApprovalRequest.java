package com.smartprocurement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_requests")
public class ApprovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_type", nullable = false)
    private ApprovalType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "created_by", nullable = false)
    private ApprovalActor createdBy = ApprovalActor.AI_AGENT;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "centre_id", nullable = false)
    private ProcurementCentre centre;

    @Column(length = 500)
    private String reason;

    @Column(name = "requested_date")
    private LocalDate requestedDate;

    @Column(name = "requested_slot")
    private String requestedSlot;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_event")
    private VoiceEventType aiEvent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public ApprovalRequest() {
    }

    public ApprovalRequest(Long id, ApprovalType type, ApprovalStatus status, ApprovalActor createdBy,
                           Booking booking, Farmer farmer, ProcurementCentre centre, String reason,
                           LocalDate requestedDate, String requestedSlot, VoiceEventType aiEvent,
                           LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime processedAt) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.createdBy = createdBy;
        this.booking = booking;
        this.farmer = farmer;
        this.centre = centre;
        this.reason = reason;
        this.requestedDate = requestedDate;
        this.requestedSlot = requestedSlot;
        this.aiEvent = aiEvent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.processedAt = processedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ApprovalType getType() { return type; }
    public void setType(ApprovalType type) { this.type = type; }

    public ApprovalStatus getStatus() { return status; }
    public void setStatus(ApprovalStatus status) { this.status = status; }

    public ApprovalActor getCreatedBy() { return createdBy; }
    public void setCreatedBy(ApprovalActor createdBy) { this.createdBy = createdBy; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }

    public ProcurementCentre getCentre() { return centre; }
    public void setCentre(ProcurementCentre centre) { this.centre = centre; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDate getRequestedDate() { return requestedDate; }
    public void setRequestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; }

    public String getRequestedSlot() { return requestedSlot; }
    public void setRequestedSlot(String requestedSlot) { this.requestedSlot = requestedSlot; }

    public VoiceEventType getAiEvent() { return aiEvent; }
    public void setAiEvent(VoiceEventType aiEvent) { this.aiEvent = aiEvent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public static ApprovalRequestBuilder builder() {
        return new ApprovalRequestBuilder();
    }

    public static class ApprovalRequestBuilder {
        private Long id;
        private ApprovalType type;
        private ApprovalStatus status = ApprovalStatus.PENDING;
        private ApprovalActor createdBy = ApprovalActor.AI_AGENT;
        private Booking booking;
        private Farmer farmer;
        private ProcurementCentre centre;
        private String reason;
        private LocalDate requestedDate;
        private String requestedSlot;
        private VoiceEventType aiEvent;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime processedAt;

        public ApprovalRequestBuilder id(Long id) { this.id = id; return this; }
        public ApprovalRequestBuilder type(ApprovalType type) { this.type = type; return this; }
        public ApprovalRequestBuilder status(ApprovalStatus status) { this.status = status; return this; }
        public ApprovalRequestBuilder createdBy(ApprovalActor createdBy) { this.createdBy = createdBy; return this; }
        public ApprovalRequestBuilder booking(Booking booking) { this.booking = booking; return this; }
        public ApprovalRequestBuilder farmer(Farmer farmer) { this.farmer = farmer; return this; }
        public ApprovalRequestBuilder centre(ProcurementCentre centre) { this.centre = centre; return this; }
        public ApprovalRequestBuilder reason(String reason) { this.reason = reason; return this; }
        public ApprovalRequestBuilder requestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; return this; }
        public ApprovalRequestBuilder requestedSlot(String requestedSlot) { this.requestedSlot = requestedSlot; return this; }
        public ApprovalRequestBuilder aiEvent(VoiceEventType aiEvent) { this.aiEvent = aiEvent; return this; }
        public ApprovalRequestBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ApprovalRequestBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public ApprovalRequestBuilder processedAt(LocalDateTime processedAt) { this.processedAt = processedAt; return this; }

        public ApprovalRequest build() {
            return new ApprovalRequest(id, type, status, createdBy, booking, farmer, centre, reason,
                    requestedDate, requestedSlot, aiEvent, createdAt, updatedAt, processedAt);
        }
    }
}
