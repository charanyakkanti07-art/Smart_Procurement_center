package com.smartprocurement.dto;

import com.smartprocurement.entity.ApprovalActor;
import com.smartprocurement.entity.ApprovalStatus;
import com.smartprocurement.entity.ApprovalType;
import com.smartprocurement.entity.VoiceEventType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ApprovalRequestDTO {
    private Long id;
    private ApprovalType type;
    private ApprovalStatus status;
    private ApprovalActor createdBy;
    private Long bookingId;
    private Long farmerId;
    private String farmerName;
    private String farmerPhone;
    private Long centreId;
    private String centreName;
    private String currentSlot;
    private LocalDate currentDate;
    private String requestedSlot;
    private LocalDate requestedDate;
    private String reason;
    private VoiceEventType aiEvent;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public ApprovalRequestDTO() {
    }

    public ApprovalRequestDTO(Long id, ApprovalType type, ApprovalStatus status, ApprovalActor createdBy,
                              Long bookingId, Long farmerId, String farmerName, String farmerPhone,
                              Long centreId, String centreName, String currentSlot, LocalDate currentDate,
                              String requestedSlot, LocalDate requestedDate, String reason,
                              VoiceEventType aiEvent, LocalDateTime createdAt, LocalDateTime processedAt) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.createdBy = createdBy;
        this.bookingId = bookingId;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.farmerPhone = farmerPhone;
        this.centreId = centreId;
        this.centreName = centreName;
        this.currentSlot = currentSlot;
        this.currentDate = currentDate;
        this.requestedSlot = requestedSlot;
        this.requestedDate = requestedDate;
        this.reason = reason;
        this.aiEvent = aiEvent;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ApprovalType getType() { return type; }
    public void setType(ApprovalType type) { this.type = type; }

    public ApprovalStatus getStatus() { return status; }
    public void setStatus(ApprovalStatus status) { this.status = status; }

    public ApprovalActor getCreatedBy() { return createdBy; }
    public void setCreatedBy(ApprovalActor createdBy) { this.createdBy = createdBy; }

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

    public String getCurrentSlot() { return currentSlot; }
    public void setCurrentSlot(String currentSlot) { this.currentSlot = currentSlot; }

    public LocalDate getCurrentDate() { return currentDate; }
    public void setCurrentDate(LocalDate currentDate) { this.currentDate = currentDate; }

    public String getRequestedSlot() { return requestedSlot; }
    public void setRequestedSlot(String requestedSlot) { this.requestedSlot = requestedSlot; }

    public LocalDate getRequestedDate() { return requestedDate; }
    public void setRequestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public VoiceEventType getAiEvent() { return aiEvent; }
    public void setAiEvent(VoiceEventType aiEvent) { this.aiEvent = aiEvent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public static ApprovalRequestDTOBuilder builder() {
        return new ApprovalRequestDTOBuilder();
    }

    public static class ApprovalRequestDTOBuilder {
        private Long id;
        private ApprovalType type;
        private ApprovalStatus status;
        private ApprovalActor createdBy;
        private Long bookingId;
        private Long farmerId;
        private String farmerName;
        private String farmerPhone;
        private Long centreId;
        private String centreName;
        private String currentSlot;
        private LocalDate currentDate;
        private String requestedSlot;
        private LocalDate requestedDate;
        private String reason;
        private VoiceEventType aiEvent;
        private LocalDateTime createdAt;
        private LocalDateTime processedAt;

        public ApprovalRequestDTOBuilder id(Long id) { this.id = id; return this; }
        public ApprovalRequestDTOBuilder type(ApprovalType type) { this.type = type; return this; }
        public ApprovalRequestDTOBuilder status(ApprovalStatus status) { this.status = status; return this; }
        public ApprovalRequestDTOBuilder createdBy(ApprovalActor createdBy) { this.createdBy = createdBy; return this; }
        public ApprovalRequestDTOBuilder bookingId(Long bookingId) { this.bookingId = bookingId; return this; }
        public ApprovalRequestDTOBuilder farmerId(Long farmerId) { this.farmerId = farmerId; return this; }
        public ApprovalRequestDTOBuilder farmerName(String farmerName) { this.farmerName = farmerName; return this; }
        public ApprovalRequestDTOBuilder farmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; return this; }
        public ApprovalRequestDTOBuilder centreId(Long centreId) { this.centreId = centreId; return this; }
        public ApprovalRequestDTOBuilder centreName(String centreName) { this.centreName = centreName; return this; }
        public ApprovalRequestDTOBuilder currentSlot(String currentSlot) { this.currentSlot = currentSlot; return this; }
        public ApprovalRequestDTOBuilder currentDate(LocalDate currentDate) { this.currentDate = currentDate; return this; }
        public ApprovalRequestDTOBuilder requestedSlot(String requestedSlot) { this.requestedSlot = requestedSlot; return this; }
        public ApprovalRequestDTOBuilder requestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; return this; }
        public ApprovalRequestDTOBuilder reason(String reason) { this.reason = reason; return this; }
        public ApprovalRequestDTOBuilder aiEvent(VoiceEventType aiEvent) { this.aiEvent = aiEvent; return this; }
        public ApprovalRequestDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ApprovalRequestDTOBuilder processedAt(LocalDateTime processedAt) { this.processedAt = processedAt; return this; }

        public ApprovalRequestDTO build() {
            return new ApprovalRequestDTO(id, type, status, createdBy, bookingId, farmerId, farmerName, farmerPhone,
                    centreId, centreName, currentSlot, currentDate, requestedSlot, requestedDate, reason, aiEvent, createdAt, processedAt);
        }
    }
}
