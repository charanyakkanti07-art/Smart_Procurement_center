package com.smartprocurement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private String paymentCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "procurement_id", nullable = false)
    private Procurement procurement;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String transactionId;
    private String providerReference;
    private String failureReason;

    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Payment() {
    }

    public Payment(Long paymentId, String paymentCode, Procurement procurement, Farmer farmer, Double amount, PaymentMethod paymentMethod, PaymentStatus status, String transactionId, String providerReference, String failureReason, LocalDateTime initiatedAt, LocalDateTime completedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.paymentId = paymentId;
        this.paymentCode = paymentCode;
        this.procurement = procurement;
        this.farmer = farmer;
        this.amount = amount;
        this.paymentMethod = paymentMethod != null ? paymentMethod : PaymentMethod.DIRECT_BENEFIT_TRANSFER;
        this.status = status != null ? status : PaymentStatus.INITIATED;
        this.transactionId = transactionId;
        this.providerReference = providerReference;
        this.failureReason = failureReason;
        this.initiatedAt = initiatedAt != null ? initiatedAt : LocalDateTime.now();
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.initiatedAt == null) this.initiatedAt = LocalDateTime.now();
        if (this.status == null) this.status = PaymentStatus.INITIATED;
        if (this.paymentMethod == null) this.paymentMethod = PaymentMethod.DIRECT_BENEFIT_TRANSFER;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public String getPaymentCode() { return paymentCode; }
    public void setPaymentCode(String paymentCode) { this.paymentCode = paymentCode; }

    public Procurement getProcurement() { return procurement; }
    public void setProcurement(Procurement procurement) { this.procurement = procurement; }

    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getProviderReference() { return providerReference; }
    public void setProviderReference(String providerReference) { this.providerReference = providerReference; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public LocalDateTime getInitiatedAt() { return initiatedAt; }
    public void setInitiatedAt(LocalDateTime initiatedAt) { this.initiatedAt = initiatedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static PaymentBuilder builder() {
        return new PaymentBuilder();
    }

    public static class PaymentBuilder {
        private Long paymentId;
        private String paymentCode;
        private Procurement procurement;
        private Farmer farmer;
        private Double amount;
        private PaymentMethod paymentMethod;
        private PaymentStatus status;
        private String transactionId;
        private String providerReference;
        private String failureReason;
        private LocalDateTime initiatedAt;
        private LocalDateTime completedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public PaymentBuilder paymentId(Long paymentId) { this.paymentId = paymentId; return this; }
        public PaymentBuilder paymentCode(String paymentCode) { this.paymentCode = paymentCode; return this; }
        public PaymentBuilder procurement(Procurement procurement) { this.procurement = procurement; return this; }
        public PaymentBuilder farmer(Farmer farmer) { this.farmer = farmer; return this; }
        public PaymentBuilder amount(Double amount) { this.amount = amount; return this; }
        public PaymentBuilder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public PaymentBuilder status(PaymentStatus status) { this.status = status; return this; }
        public PaymentBuilder transactionId(String transactionId) { this.transactionId = transactionId; return this; }
        public PaymentBuilder providerReference(String providerReference) { this.providerReference = providerReference; return this; }
        public PaymentBuilder failureReason(String failureReason) { this.failureReason = failureReason; return this; }
        public PaymentBuilder initiatedAt(LocalDateTime initiatedAt) { this.initiatedAt = initiatedAt; return this; }
        public PaymentBuilder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }
        public PaymentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public PaymentBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Payment build() {
            return new Payment(paymentId, paymentCode, procurement, farmer, amount, paymentMethod, status, transactionId, providerReference, failureReason, initiatedAt, completedAt, createdAt, updatedAt);
        }
    }
}
