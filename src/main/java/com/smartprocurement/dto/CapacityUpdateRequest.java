package com.smartprocurement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CapacityUpdateRequest {

    @NotNull(message = "Total capacity is required")
    @Positive(message = "Total capacity must be positive")
    private Double totalCapacity;

    public CapacityUpdateRequest() {
    }

    public CapacityUpdateRequest(Double totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public Double getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(Double totalCapacity) { this.totalCapacity = totalCapacity; }
}
