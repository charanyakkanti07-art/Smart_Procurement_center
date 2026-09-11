package com.smartprocurement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deposit_audit_logs")
public class DepositAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    private Long bookingId;

    private Long farmerId;

    private String eventType;

    @Enumerated(EnumType.STRING)
    private DepositStatus previousStatus;

    @Enumerated(EnumType.STRING)
    private DepositStatus newStatus;

    private Double amount;

    private String transactionRef;

    @Column(length = 1000)
    private String details;

    private LocalDateTime createdAt;

    public DepositAuditLog() {
    }

    public DepositAuditLog(Long bookingId, Long farmerId, String eventType, DepositStatus previousStatus, DepositStatus newStatus, Double amount, String transactionRef, String details) {
        this.bookingId = bookingId;
        this.farmerId = farmerId;
        this.eventType = eventType;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.amount = amount;
        this.transactionRef = transactionRef;
        this.details = details;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getAuditId() { return auditId; }
    public void setAuditId(Long auditId) { this.auditId = auditId; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public DepositStatus getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(DepositStatus previousStatus) { this.previousStatus = previousStatus; }

    public DepositStatus getNewStatus() { return newStatus; }
    public void setNewStatus(DepositStatus newStatus) { this.newStatus = newStatus; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
