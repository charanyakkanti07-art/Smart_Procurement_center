package com.smartprocurement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CropRequest {

    @NotBlank(message = "Crop type is required")
    private String cropType;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Double quantity;

    public CropRequest() {
    }

    public CropRequest(String cropType, Double quantity) {
        this.cropType = cropType;
        this.quantity = quantity;
    }

    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
}
