package com.smartprocurement.dto;

import java.time.LocalDate;

public class AssignSlotRequest {
    private LocalDate newDate;
    private String newSlot;

    public AssignSlotRequest() {
    }

    public AssignSlotRequest(LocalDate newDate, String newSlot) {
        this.newDate = newDate;
        this.newSlot = newSlot;
    }

    public LocalDate getNewDate() { return newDate; }
    public void setNewDate(LocalDate newDate) { this.newDate = newDate; }

    public String getNewSlot() { return newSlot; }
    public void setNewSlot(String newSlot) { this.newSlot = newSlot; }
}
