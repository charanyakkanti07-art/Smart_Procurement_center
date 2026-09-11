package com.smartprocurement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_deposits")
public class BookingDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long depositId;

    private Long bookingId;

    private Long farmerId;

    private Long procurementCentreId;

    private String slotId;

    private Double depositAmount = 300.0;

    private String currency = "INR";

    private String paymentTransactionId;

    private String paymentStatus;

    @Enumerated(EnumType.STRING)
    private DepositStatus depositStatus;

    private LocalDateTime paymentTimestamp;

    private LocalDateTime refundTimestamp;

    private String refundTransactionId;

    private String refundReason;

    private Double refundAmount;

    private Boolean policyAcknowledged = true;

    // Razorpay Integration Fields
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    private Boolean signatureVerified = false;
    private String razorpayRefundId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public BookingDeposit() {
    }

    public BookingDeposit(Long bookingId, Long farmerId, Long procurementCentreId, String slotId, Double depositAmount, String paymentTransactionId, String paymentStatus, DepositStatus depositStatus) {
        this.bookingId = bookingId;
        this.farmerId = farmerId;
        this.procurementCentreId = procurementCentreId;
        this.slotId = slotId;
        this.depositAmount = depositAmount != null ? depositAmount : 300.0;
        this.currency = "INR";
        this.paymentTransactionId = paymentTransactionId;
        this.paymentStatus = paymentStatus;
        this.depositStatus = depositStatus != null ? depositStatus : DepositStatus.PENDING;
        this.policyAcknowledged = true;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.depositAmount == null) this.depositAmount = 300.0;
        if (this.currency == null) this.currency = "INR";
        if (this.depositStatus == null) this.depositStatus = DepositStatus.PENDING;
        if (this.policyAcknowledged == null) this.policyAcknowledged = true;
        if (this.signatureVerified == null) this.signatureVerified = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getDepositId() { return depositId; }
    public void setDepositId(Long depositId) { this.depositId = depositId; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public Long getProcurementCentreId() { return procurementCentreId; }
    public void setProcurementCentreId(Long procurementCentreId) { this.procurementCentreId = procurementCentreId; }

    public String getSlotId() { return slotId; }
    public void setSlotId(String slotId) { this.slotId = slotId; }

    public Double getDepositAmount() { return depositAmount; }
    public void setDepositAmount(Double depositAmount) { this.depositAmount = depositAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPaymentTransactionId() { return paymentTransactionId; }
    public void setPaymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public DepositStatus getDepositStatus() { return depositStatus; }
    public void setDepositStatus(DepositStatus depositStatus) { this.depositStatus = depositStatus; }

    public LocalDateTime getPaymentTimestamp() { return paymentTimestamp; }
    public void setPaymentTimestamp(LocalDateTime paymentTimestamp) { this.paymentTimestamp = paymentTimestamp; }

    public LocalDateTime getRefundTimestamp() { return refundTimestamp; }
    public void setRefundTimestamp(LocalDateTime refundTimestamp) { this.refundTimestamp = refundTimestamp; }

    public String getRefundTransactionId() { return refundTransactionId; }
    public void setRefundTransactionId(String refundTransactionId) { this.refundTransactionId = refundTransactionId; }

    public String getRefundReason() { return refundReason; }
    public void setRefundReason(String refundReason) { this.refundReason = refundReason; }

    public Double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }

    public Boolean getPolicyAcknowledged() { return policyAcknowledged; }
    public void setPolicyAcknowledged(Boolean policyAcknowledged) { this.policyAcknowledged = policyAcknowledged; }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; }

    public Boolean getSignatureVerified() { return signatureVerified; }
    public void setSignatureVerified(Boolean signatureVerified) { this.signatureVerified = signatureVerified; }

    public String getRazorpayRefundId() { return razorpayRefundId; }
    public void setRazorpayRefundId(String razorpayRefundId) { this.razorpayRefundId = razorpayRefundId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}
