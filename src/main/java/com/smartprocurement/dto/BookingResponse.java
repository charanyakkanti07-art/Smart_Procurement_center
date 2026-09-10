package com.smartprocurement.dto;

import com.smartprocurement.entity.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingResponse {
    private Long bookingId;
    private Long farmerId;
    private String farmerName;
    private Long centreId;
    private String centreName;
    private Long cropId;
    private String cropType;
    private Double quantity;
    private LocalDate bookingDate;
    private String slot;
    private BookingStatus status;
    private String reason;
    private Double availableCapacity;
    private LocalDateTime createdAt;

    public BookingResponse() {
    }

    public BookingResponse(Long bookingId, Long farmerId, String farmerName, Long centreId, String centreName, Long cropId, String cropType, Double quantity, LocalDate bookingDate, String slot, BookingStatus status, String reason, Double availableCapacity, LocalDateTime createdAt) {
        this.bookingId = bookingId;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.centreId = centreId;
        this.centreName = centreName;
        this.cropId = cropId;
        this.cropType = cropType;
        this.quantity = quantity;
        this.bookingDate = bookingDate;
        this.slot = slot;
        this.status = status;
        this.reason = reason;
        this.availableCapacity = availableCapacity;
        this.createdAt = createdAt;
    }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public Long getCentreId() { return centreId; }
    public void setCentreId(Long centreId) { this.centreId = centreId; }

    public String getCentreName() { return centreName; }
    public void setCentreName(String centreName) { this.centreName = centreName; }

    public Long getCropId() { return cropId; }
    public void setCropId(Long cropId) { this.cropId = cropId; }

    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Double getAvailableCapacity() { return availableCapacity; }
    public void setAvailableCapacity(Double availableCapacity) { this.availableCapacity = availableCapacity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static BookingResponseBuilder builder() {
        return new BookingResponseBuilder();
    }

    public static class BookingResponseBuilder {
        private Long bookingId;
        private Long farmerId;
        private String farmerName;
        private Long centreId;
        private String centreName;
        private Long cropId;
        private String cropType;
        private Double quantity;
        private LocalDate bookingDate;
        private String slot;
        private BookingStatus status;
        private String reason;
        private Double availableCapacity;
        private LocalDateTime createdAt;

        public BookingResponseBuilder bookingId(Long bookingId) { this.bookingId = bookingId; return this; }
        public BookingResponseBuilder farmerId(Long farmerId) { this.farmerId = farmerId; return this; }
        public BookingResponseBuilder farmerName(String farmerName) { this.farmerName = farmerName; return this; }
        public BookingResponseBuilder centreId(Long centreId) { this.centreId = centreId; return this; }
        public BookingResponseBuilder centreName(String centreName) { this.centreName = centreName; return this; }
        public BookingResponseBuilder cropId(Long cropId) { this.cropId = cropId; return this; }
        public BookingResponseBuilder cropType(String cropType) { this.cropType = cropType; return this; }
        public BookingResponseBuilder quantity(Double quantity) { this.quantity = quantity; return this; }
        public BookingResponseBuilder bookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; return this; }
        public BookingResponseBuilder slot(String slot) { this.slot = slot; return this; }
        public BookingResponseBuilder status(BookingStatus status) { this.status = status; return this; }
        public BookingResponseBuilder reason(String reason) { this.reason = reason; return this; }
        public BookingResponseBuilder availableCapacity(Double availableCapacity) { this.availableCapacity = availableCapacity; return this; }
        public BookingResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public BookingResponse build() {
            return new BookingResponse(bookingId, farmerId, farmerName, centreId, centreName, cropId, cropType, quantity, bookingDate, slot, status, reason, availableCapacity, createdAt);
        }
    }
}
