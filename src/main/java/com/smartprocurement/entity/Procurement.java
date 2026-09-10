package com.smartprocurement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "procurements")
public class Procurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long procurementId;

    private String procurementCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "centre_id", nullable = false)
    private ProcurementCentre centre;

    private Double grossWeight;
    private Double tareWeight;
    private Double netQuantity;
    private String unit;

    private String qualityGrade;

    @Column(columnDefinition = "TEXT")
    private String qualityParameters;

    @Enumerated(EnumType.STRING)
    private QualityStatus qualityStatus;

    private Double ratePerUnit;
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private ProcurementStatus status;

    private String verifiedBy;
    private LocalDateTime verifiedAt;

    private String weighedBy;
    private LocalDateTime weighedAt;

    private String qualityCheckedBy;
    private LocalDateTime qualityCheckedAt;

    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Procurement() {
    }

    public Procurement(Long procurementId, String procurementCode, Booking booking, Farmer farmer, ProcurementCentre centre, Double grossWeight, Double tareWeight, Double netQuantity, String unit, String qualityGrade, String qualityParameters, QualityStatus qualityStatus, Double ratePerUnit, Double totalAmount, ProcurementStatus status, String verifiedBy, LocalDateTime verifiedAt, String weighedBy, LocalDateTime weighedAt, String qualityCheckedBy, LocalDateTime qualityCheckedAt, LocalDateTime completedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.procurementId = procurementId;
        this.procurementCode = procurementCode;
        this.booking = booking;
        this.farmer = farmer;
        this.centre = centre;
        this.grossWeight = grossWeight;
        this.tareWeight = tareWeight;
        this.netQuantity = netQuantity;
        this.unit = unit != null ? unit : "kg";
        this.qualityGrade = qualityGrade;
        this.qualityParameters = qualityParameters;
        this.qualityStatus = qualityStatus;
        this.ratePerUnit = ratePerUnit;
        this.totalAmount = totalAmount;
        this.status = status != null ? status : ProcurementStatus.ARRIVED;
        this.verifiedBy = verifiedBy;
        this.verifiedAt = verifiedAt;
        this.weighedBy = weighedBy;
        this.weighedAt = weighedAt;
        this.qualityCheckedBy = qualityCheckedBy;
        this.qualityCheckedAt = qualityCheckedAt;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.unit == null) this.unit = "kg";
        if (this.status == null) this.status = ProcurementStatus.ARRIVED;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getProcurementId() { return procurementId; }
    public void setProcurementId(Long procurementId) { this.procurementId = procurementId; }

    public String getProcurementCode() { return procurementCode; }
    public void setProcurementCode(String procurementCode) { this.procurementCode = procurementCode; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }

    public ProcurementCentre getCentre() { return centre; }
    public void setCentre(ProcurementCentre centre) { this.centre = centre; }

    public Double getGrossWeight() { return grossWeight; }
    public void setGrossWeight(Double grossWeight) { this.grossWeight = grossWeight; }

    public Double getTareWeight() { return tareWeight; }
    public void setTareWeight(Double tareWeight) { this.tareWeight = tareWeight; }

    public Double getNetQuantity() { return netQuantity; }
    public void setNetQuantity(Double netQuantity) { this.netQuantity = netQuantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getQualityGrade() { return qualityGrade; }
    public void setQualityGrade(String qualityGrade) { this.qualityGrade = qualityGrade; }

    public String getQualityParameters() { return qualityParameters; }
    public void setQualityParameters(String qualityParameters) { this.qualityParameters = qualityParameters; }

    public QualityStatus getQualityStatus() { return qualityStatus; }
    public void setQualityStatus(QualityStatus qualityStatus) { this.qualityStatus = qualityStatus; }

    public Double getRatePerUnit() { return ratePerUnit; }
    public void setRatePerUnit(Double ratePerUnit) { this.ratePerUnit = ratePerUnit; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public ProcurementStatus getStatus() { return status; }
    public void setStatus(ProcurementStatus status) { this.status = status; }

    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public String getWeighedBy() { return weighedBy; }
    public void setWeighedBy(String weighedBy) { this.weighedBy = weighedBy; }

    public LocalDateTime getWeighedAt() { return weighedAt; }
    public void setWeighedAt(LocalDateTime weighedAt) { this.weighedAt = weighedAt; }

    public String getQualityCheckedBy() { return qualityCheckedBy; }
    public void setQualityCheckedBy(String qualityCheckedBy) { this.qualityCheckedBy = qualityCheckedBy; }

    public LocalDateTime getQualityCheckedAt() { return qualityCheckedAt; }
    public void setQualityCheckedAt(LocalDateTime qualityCheckedAt) { this.qualityCheckedAt = qualityCheckedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static ProcurementBuilder builder() {
        return new ProcurementBuilder();
    }

    public static class ProcurementBuilder {
        private Long procurementId;
        private String procurementCode;
        private Booking booking;
        private Farmer farmer;
        private ProcurementCentre centre;
        private Double grossWeight;
        private Double tareWeight;
        private Double netQuantity;
        private String unit;
        private String qualityGrade;
        private String qualityParameters;
        private QualityStatus qualityStatus;
        private Double ratePerUnit;
        private Double totalAmount;
        private ProcurementStatus status;
        private String verifiedBy;
        private LocalDateTime verifiedAt;
        private String weighedBy;
        private LocalDateTime weighedAt;
        private String qualityCheckedBy;
        private LocalDateTime qualityCheckedAt;
        private LocalDateTime completedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ProcurementBuilder procurementId(Long procurementId) { this.procurementId = procurementId; return this; }
        public ProcurementBuilder procurementCode(String procurementCode) { this.procurementCode = procurementCode; return this; }
        public ProcurementBuilder booking(Booking booking) { this.booking = booking; return this; }
        public ProcurementBuilder farmer(Farmer farmer) { this.farmer = farmer; return this; }
        public ProcurementBuilder centre(ProcurementCentre centre) { this.centre = centre; return this; }
        public ProcurementBuilder grossWeight(Double grossWeight) { this.grossWeight = grossWeight; return this; }
        public ProcurementBuilder tareWeight(Double tareWeight) { this.tareWeight = tareWeight; return this; }
        public ProcurementBuilder netQuantity(Double netQuantity) { this.netQuantity = netQuantity; return this; }
        public ProcurementBuilder unit(String unit) { this.unit = unit; return this; }
        public ProcurementBuilder qualityGrade(String qualityGrade) { this.qualityGrade = qualityGrade; return this; }
        public ProcurementBuilder qualityParameters(String qualityParameters) { this.qualityParameters = qualityParameters; return this; }
        public ProcurementBuilder qualityStatus(QualityStatus qualityStatus) { this.qualityStatus = qualityStatus; return this; }
        public ProcurementBuilder ratePerUnit(Double ratePerUnit) { this.ratePerUnit = ratePerUnit; return this; }
        public ProcurementBuilder totalAmount(Double totalAmount) { this.totalAmount = totalAmount; return this; }
        public ProcurementBuilder status(ProcurementStatus status) { this.status = status; return this; }
        public ProcurementBuilder verifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; return this; }
        public ProcurementBuilder verifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; return this; }
        public ProcurementBuilder weighedBy(String weighedBy) { this.weighedBy = weighedBy; return this; }
        public ProcurementBuilder weighedAt(LocalDateTime weighedAt) { this.weighedAt = weighedAt; return this; }
        public ProcurementBuilder qualityCheckedBy(String qualityCheckedBy) { this.qualityCheckedBy = qualityCheckedBy; return this; }
        public ProcurementBuilder qualityCheckedAt(LocalDateTime qualityCheckedAt) { this.qualityCheckedAt = qualityCheckedAt; return this; }
        public ProcurementBuilder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }
        public ProcurementBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ProcurementBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Procurement build() {
            return new Procurement(procurementId, procurementCode, booking, farmer, centre, grossWeight, tareWeight, netQuantity, unit, qualityGrade, qualityParameters, qualityStatus, ratePerUnit, totalAmount, status, verifiedBy, verifiedAt, weighedBy, weighedAt, qualityCheckedBy, qualityCheckedAt, completedAt, createdAt, updatedAt);
        }
    }
}
