package com.smartprocurement.dto;

import com.smartprocurement.entity.BookingStatus;

import java.time.LocalDateTime;

public class QueueStatusDTO {
    private Long queueEntryId;
    private Long bookingId;
    private Long farmerId;
    private String farmerName;
    private Long centreId;
    private String centreName;
    private String tokenNumber;
    private BookingStatus status;
    private Integer queuePosition;
    private Integer farmersAhead;
    private String currentlyProcessingToken;
    private Integer estimatedWaitMinutes;
    private String estimatedWaitFormatted;
    private LocalDateTime arrivalTime;
    private LocalDateTime calledTime;
    private LocalDateTime processingStartTime;
    private LocalDateTime processingEndTime;
    private String cropType;
    private Double quantity;

    public QueueStatusDTO() {}

    public QueueStatusDTO(Long queueEntryId, Long bookingId, Long farmerId, String farmerName, Long centreId, String centreName, String tokenNumber, BookingStatus status, Integer queuePosition, Integer farmersAhead, String currentlyProcessingToken, Integer estimatedWaitMinutes, String estimatedWaitFormatted, LocalDateTime arrivalTime, LocalDateTime calledTime, LocalDateTime processingStartTime, LocalDateTime processingEndTime, String cropType, Double quantity) {
        this.queueEntryId = queueEntryId;
        this.bookingId = bookingId;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.centreId = centreId;
        this.centreName = centreName;
        this.tokenNumber = tokenNumber;
        this.status = status;
        this.queuePosition = queuePosition;
        this.farmersAhead = farmersAhead;
        this.currentlyProcessingToken = currentlyProcessingToken;
        this.estimatedWaitMinutes = estimatedWaitMinutes;
        this.estimatedWaitFormatted = estimatedWaitFormatted;
        this.arrivalTime = arrivalTime;
        this.calledTime = calledTime;
        this.processingStartTime = processingStartTime;
        this.processingEndTime = processingEndTime;
        this.cropType = cropType;
        this.quantity = quantity;
    }

    public Long getQueueEntryId() { return queueEntryId; }
    public void setQueueEntryId(Long queueEntryId) { this.queueEntryId = queueEntryId; }

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

    public String getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(String tokenNumber) { this.tokenNumber = tokenNumber; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public Integer getQueuePosition() { return queuePosition; }
    public void setQueuePosition(Integer queuePosition) { this.queuePosition = queuePosition; }

    public Integer getFarmersAhead() { return farmersAhead; }
    public void setFarmersAhead(Integer farmersAhead) { this.farmersAhead = farmersAhead; }

    public String getCurrentlyProcessingToken() { return currentlyProcessingToken; }
    public void setCurrentlyProcessingToken(String currentlyProcessingToken) { this.currentlyProcessingToken = currentlyProcessingToken; }

    public Integer getEstimatedWaitMinutes() { return estimatedWaitMinutes; }
    public void setEstimatedWaitMinutes(Integer estimatedWaitMinutes) { this.estimatedWaitMinutes = estimatedWaitMinutes; }

    public String getEstimatedWaitFormatted() { return estimatedWaitFormatted; }
    public void setEstimatedWaitFormatted(String estimatedWaitFormatted) { this.estimatedWaitFormatted = estimatedWaitFormatted; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public LocalDateTime getCalledTime() { return calledTime; }
    public void setCalledTime(LocalDateTime calledTime) { this.calledTime = calledTime; }

    public LocalDateTime getProcessingStartTime() { return processingStartTime; }
    public void setProcessingStartTime(LocalDateTime processingStartTime) { this.processingStartTime = processingStartTime; }

    public LocalDateTime getProcessingEndTime() { return processingEndTime; }
    public void setProcessingEndTime(LocalDateTime processingEndTime) { this.processingEndTime = processingEndTime; }

    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public static QueueStatusDTOBuilder builder() {
        return new QueueStatusDTOBuilder();
    }

    public static class QueueStatusDTOBuilder {
        private Long queueEntryId;
        private Long bookingId;
        private Long farmerId;
        private String farmerName;
        private Long centreId;
        private String centreName;
        private String tokenNumber;
        private BookingStatus status;
        private Integer queuePosition;
        private Integer farmersAhead;
        private String currentlyProcessingToken;
        private Integer estimatedWaitMinutes;
        private String estimatedWaitFormatted;
        private LocalDateTime arrivalTime;
        private LocalDateTime calledTime;
        private LocalDateTime processingStartTime;
        private LocalDateTime processingEndTime;
        private String cropType;
        private Double quantity;

        public QueueStatusDTOBuilder queueEntryId(Long queueEntryId) { this.queueEntryId = queueEntryId; return this; }
        public QueueStatusDTOBuilder bookingId(Long bookingId) { this.bookingId = bookingId; return this; }
        public QueueStatusDTOBuilder farmerId(Long farmerId) { this.farmerId = farmerId; return this; }
        public QueueStatusDTOBuilder farmerName(String farmerName) { this.farmerName = farmerName; return this; }
        public QueueStatusDTOBuilder centreId(Long centreId) { this.centreId = centreId; return this; }
        public QueueStatusDTOBuilder centreName(String centreName) { this.centreName = centreName; return this; }
        public QueueStatusDTOBuilder tokenNumber(String tokenNumber) { this.tokenNumber = tokenNumber; return this; }
        public QueueStatusDTOBuilder status(BookingStatus status) { this.status = status; return this; }
        public QueueStatusDTOBuilder queuePosition(Integer queuePosition) { this.queuePosition = queuePosition; return this; }
        public QueueStatusDTOBuilder farmersAhead(Integer farmersAhead) { this.farmersAhead = farmersAhead; return this; }
        public QueueStatusDTOBuilder currentlyProcessingToken(String currentlyProcessingToken) { this.currentlyProcessingToken = currentlyProcessingToken; return this; }
        public QueueStatusDTOBuilder estimatedWaitMinutes(Integer estimatedWaitMinutes) { this.estimatedWaitMinutes = estimatedWaitMinutes; return this; }
        public QueueStatusDTOBuilder estimatedWaitFormatted(String estimatedWaitFormatted) { this.estimatedWaitFormatted = estimatedWaitFormatted; return this; }
        public QueueStatusDTOBuilder arrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; return this; }
        public QueueStatusDTOBuilder calledTime(LocalDateTime calledTime) { this.calledTime = calledTime; return this; }
        public QueueStatusDTOBuilder processingStartTime(LocalDateTime processingStartTime) { this.processingStartTime = processingStartTime; return this; }
        public QueueStatusDTOBuilder processingEndTime(LocalDateTime processingEndTime) { this.processingEndTime = processingEndTime; return this; }
        public QueueStatusDTOBuilder cropType(String cropType) { this.cropType = cropType; return this; }
        public QueueStatusDTOBuilder quantity(Double quantity) { this.quantity = quantity; return this; }

        public QueueStatusDTO build() {
            return new QueueStatusDTO(queueEntryId, bookingId, farmerId, farmerName, centreId, centreName, tokenNumber, status, queuePosition, farmersAhead, currentlyProcessingToken, estimatedWaitMinutes, estimatedWaitFormatted, arrivalTime, calledTime, processingStartTime, processingEndTime, cropType, quantity);
        }
    }
}
