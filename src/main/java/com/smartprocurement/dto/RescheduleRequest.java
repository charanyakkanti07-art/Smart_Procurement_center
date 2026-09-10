package com.smartprocurement.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class RescheduleRequest {

    @NotNull(message = "New booking date is required")
    @FutureOrPresent(message = "New booking date must be today or in the future")
    private LocalDate bookingDate;

    @NotBlank(message = "New slot is required")
    private String slot;

    public RescheduleRequest() {
    }

    public RescheduleRequest(LocalDate bookingDate, String slot) {
        this.bookingDate = bookingDate;
        this.slot = slot;
    }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }
}
