package com.smartprocurement.dto;

import com.smartprocurement.entity.QualityStatus;

public class ProcurementRequestDTO {
    private Double grossWeight;
    private Double tareWeight;
    private String unit;

    private String qualityGrade;
    private String qualityParameters;
    private QualityStatus qualityStatus;

    private String operatorName;

    public ProcurementRequestDTO() {
    }

    public Double getGrossWeight() { return grossWeight; }
    public void setGrossWeight(Double grossWeight) { this.grossWeight = grossWeight; }

    public Double getTareWeight() { return tareWeight; }
    public void setTareWeight(Double tareWeight) { this.tareWeight = tareWeight; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getQualityGrade() { return qualityGrade; }
    public void setQualityGrade(String qualityGrade) { this.qualityGrade = qualityGrade; }

    public String getQualityParameters() { return qualityParameters; }
    public void setQualityParameters(String qualityParameters) { this.qualityParameters = qualityParameters; }

    public QualityStatus getQualityStatus() { return qualityStatus; }
    public void setQualityStatus(QualityStatus qualityStatus) { this.qualityStatus = qualityStatus; }

    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
}
