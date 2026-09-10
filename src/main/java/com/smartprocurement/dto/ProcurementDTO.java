package com.smartprocurement.dto;

import com.smartprocurement.entity.ProcurementStatus;
import com.smartprocurement.entity.QualityStatus;

import java.time.LocalDateTime;

public class ProcurementDTO {
    private Long procurementId;
    private String procurementCode;
    private Long bookingId;
    private Long farmerId;
    private String farmerName;
    private String farmerPhone;
    private Long centreId;
    private String centreName;
    private String cropType;
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

    public ProcurementDTO() {
    }

    public Long getProcurementId() { return procurementId; }
    public void setProcurementId(Long procurementId) { this.procurementId = procurementId; }

    public String getProcurementCode() { return procurementCode; }
    public void setProcurementCode(String procurementCode) { this.procurementCode = procurementCode; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public Long getCentreId() { return centreId; }
    public void setCentreId(Long centreId) { this.centreId = centreId; }

    public String getCentreName() { return centreName; }
    public void setCentreName(String centreName) { this.centreName = centreName; }

    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }

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
}
