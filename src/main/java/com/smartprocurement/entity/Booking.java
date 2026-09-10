package com.smartprocurement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "centre_id", nullable = false)
    private ProcurementCentre centre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    private Double quantity;

    private LocalDate bookingDate;

    private String slot;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime createdAt;

    // Phase 5 Tracking & Timestamp Fields
    private LocalDateTime calledAt;
    private LocalDateTime arrivalTime;
    private LocalDateTime processingStartTime;
    private LocalDateTime completionTime;
    private LocalDateTime skippedAt;
    private String skipReason;
    private String cancelReason;
    private String requestedSlot;
    private LocalDate requestedDate;
    private String rescheduleReason;
    private LocalDateTime updatedAt;

    public Booking() {
    }

    public Booking(Long bookingId, Farmer farmer, ProcurementCentre centre, Crop crop, Double quantity, LocalDate bookingDate, String slot, BookingStatus status, LocalDateTime createdAt) {
        this.bookingId = bookingId;
        this.farmer = farmer;
        this.centre = centre;
        this.crop = crop;
        this.quantity = quantity;
        this.bookingDate = bookingDate;
        this.slot = slot;
        this.status = status != null ? status : BookingStatus.CONFIRMED;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = BookingStatus.CONFIRMED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }

    public ProcurementCentre getCentre() { return centre; }
    public void setCentre(ProcurementCentre centre) { this.centre = centre; }

    public Crop getCrop() { return crop; }
    public void setCrop(Crop crop) { this.crop = crop; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCalledAt() { return calledAt; }
    public void setCalledAt(LocalDateTime calledAt) { this.calledAt = calledAt; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public LocalDateTime getProcessingStartTime() { return processingStartTime; }
    public void setProcessingStartTime(LocalDateTime processingStartTime) { this.processingStartTime = processingStartTime; }

    public LocalDateTime getCompletionTime() { return completionTime; }
    public void setCompletionTime(LocalDateTime completionTime) { this.completionTime = completionTime; }

    public LocalDateTime getSkippedAt() { return skippedAt; }
    public void setSkippedAt(LocalDateTime skippedAt) { this.skippedAt = skippedAt; }

    public String getSkipReason() { return skipReason; }
    public void setSkipReason(String skipReason) { this.skipReason = skipReason; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public String getRequestedSlot() { return requestedSlot; }
    public void setRequestedSlot(String requestedSlot) { this.requestedSlot = requestedSlot; }

    public LocalDate getRequestedDate() { return requestedDate; }
    public void setRequestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; }

    public String getRescheduleReason() { return rescheduleReason; }
    public void setRescheduleReason(String rescheduleReason) { this.rescheduleReason = rescheduleReason; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static BookingBuilder builder() {
        return new BookingBuilder();
    }

    public static class BookingBuilder {
        private Long bookingId;
        private Farmer farmer;
        private ProcurementCentre centre;
        private Crop crop;
        private Double quantity;
        private LocalDate bookingDate;
        private String slot;
        private BookingStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime calledAt;
        private LocalDateTime arrivalTime;
        private LocalDateTime processingStartTime;
        private LocalDateTime completionTime;
        private LocalDateTime skippedAt;
        private String skipReason;
        private String cancelReason;
        private String requestedSlot;
        private LocalDate requestedDate;
        private String rescheduleReason;

        public BookingBuilder bookingId(Long bookingId) { this.bookingId = bookingId; return this; }
        public BookingBuilder farmer(Farmer farmer) { this.farmer = farmer; return this; }
        public BookingBuilder centre(ProcurementCentre centre) { this.centre = centre; return this; }
        public BookingBuilder crop(Crop crop) { this.crop = crop; return this; }
        public BookingBuilder quantity(Double quantity) { this.quantity = quantity; return this; }
        public BookingBuilder bookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; return this; }
        public BookingBuilder slot(String slot) { this.slot = slot; return this; }
        public BookingBuilder status(BookingStatus status) { this.status = status; return this; }
        public BookingBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public BookingBuilder calledAt(LocalDateTime calledAt) { this.calledAt = calledAt; return this; }
        public BookingBuilder arrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; return this; }
        public BookingBuilder processingStartTime(LocalDateTime processingStartTime) { this.processingStartTime = processingStartTime; return this; }
        public BookingBuilder completionTime(LocalDateTime completionTime) { this.completionTime = completionTime; return this; }
        public BookingBuilder skippedAt(LocalDateTime skippedAt) { this.skippedAt = skippedAt; return this; }
        public BookingBuilder skipReason(String skipReason) { this.skipReason = skipReason; return this; }
        public BookingBuilder cancelReason(String cancelReason) { this.cancelReason = cancelReason; return this; }
        public BookingBuilder requestedSlot(String requestedSlot) { this.requestedSlot = requestedSlot; return this; }
        public BookingBuilder requestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; return this; }
        public BookingBuilder rescheduleReason(String rescheduleReason) { this.rescheduleReason = rescheduleReason; return this; }

        public Booking build() {
            Booking b = new Booking(bookingId, farmer, centre, crop, quantity, bookingDate, slot, status, createdAt);
            b.setCalledAt(calledAt);
            b.setArrivalTime(arrivalTime);
            b.setProcessingStartTime(processingStartTime);
            b.setCompletionTime(completionTime);
            b.setSkippedAt(skippedAt);
            b.setSkipReason(skipReason);
            b.setCancelReason(cancelReason);
            b.setRequestedSlot(requestedSlot);
            b.setRequestedDate(requestedDate);
            b.setRescheduleReason(rescheduleReason);
            return b;
        }
    }
}
