package com.smartprocurement.exception;

public class CapacityExceededException extends RuntimeException {
    private final Double availableCapacity;

    public CapacityExceededException(String message, Double availableCapacity) {
        super(message);
        this.availableCapacity = availableCapacity;
    }

    public Double getAvailableCapacity() {
        return availableCapacity;
    }
}
