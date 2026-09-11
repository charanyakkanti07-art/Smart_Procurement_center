package com.smartprocurement.dto;

import com.smartprocurement.entity.NotificationChannel;
import com.smartprocurement.entity.NotificationEventType;
import com.smartprocurement.entity.NotificationStatus;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Long notificationId;
    private Long farmerId;
    private NotificationEventType eventType;
    private String title;
    private String message;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String referenceId;
    private String phoneNumber;
    private Long bookingId;
    private Long centreId;
    private Integer queuePositionBefore;
    private Integer queuePositionAfter;
    private String estimatedTimeBefore;
    private String estimatedTimeAfter;
    private String providerMessageId;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;

    public NotificationDTO() {
    }

    public NotificationDTO(Long notificationId, Long farmerId, NotificationEventType eventType, String title,
                           String message, NotificationChannel channel, NotificationStatus status,
                           String referenceId, String phoneNumber, Long bookingId, Long centreId,
                           Integer queuePositionBefore, Integer queuePositionAfter, String estimatedTimeBefore,
                           String estimatedTimeAfter, String providerMessageId, String errorMessage,
                           LocalDateTime createdAt, LocalDateTime sentAt, LocalDateTime readAt) {
        this.notificationId = notificationId;
        this.farmerId = farmerId;
        this.eventType = eventType;
        this.title = title;
        this.message = message;
        this.channel = channel;
        this.status = status;
        this.referenceId = referenceId;
        this.phoneNumber = phoneNumber;
        this.bookingId = bookingId;
        this.centreId = centreId;
        this.queuePositionBefore = queuePositionBefore;
        this.queuePositionAfter = queuePositionAfter;
        this.estimatedTimeBefore = estimatedTimeBefore;
        this.estimatedTimeAfter = estimatedTimeAfter;
        this.providerMessageId = providerMessageId;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.readAt = readAt;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(Long farmerId) {
        this.farmerId = farmerId;
    }

    public NotificationEventType getEventType() {
        return eventType;
    }

    public void setEventType(NotificationEventType eventType) {
        this.eventType = eventType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getCentreId() {
        return centreId;
    }

    public void setCentreId(Long centreId) {
        this.centreId = centreId;
    }

    public Integer getQueuePositionBefore() {
        return queuePositionBefore;
    }

    public void setQueuePositionBefore(Integer queuePositionBefore) {
        this.queuePositionBefore = queuePositionBefore;
    }

    public Integer getQueuePositionAfter() {
        return queuePositionAfter;
    }

    public void setQueuePositionAfter(Integer queuePositionAfter) {
        this.queuePositionAfter = queuePositionAfter;
    }

    public String getEstimatedTimeBefore() {
        return estimatedTimeBefore;
    }

    public void setEstimatedTimeBefore(String estimatedTimeBefore) {
        this.estimatedTimeBefore = estimatedTimeBefore;
    }

    public String getEstimatedTimeAfter() {
        return estimatedTimeAfter;
    }

    public void setEstimatedTimeAfter(String estimatedTimeAfter) {
        this.estimatedTimeAfter = estimatedTimeAfter;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public void setProviderMessageId(String providerMessageId) {
        this.providerMessageId = providerMessageId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public static NotificationDTOBuilder builder() {
        return new NotificationDTOBuilder();
    }

    public static class NotificationDTOBuilder {
        private Long notificationId;
        private Long farmerId;
        private NotificationEventType eventType;
        private String title;
        private String message;
        private NotificationChannel channel;
        private NotificationStatus status;
        private String referenceId;
        private String phoneNumber;
        private Long bookingId;
        private Long centreId;
        private Integer queuePositionBefore;
        private Integer queuePositionAfter;
        private String estimatedTimeBefore;
        private String estimatedTimeAfter;
        private String providerMessageId;
        private String errorMessage;
        private LocalDateTime createdAt;
        private LocalDateTime sentAt;
        private LocalDateTime readAt;

        public NotificationDTOBuilder notificationId(Long notificationId) {
            this.notificationId = notificationId;
            return this;
        }

        public NotificationDTOBuilder farmerId(Long farmerId) {
            this.farmerId = farmerId;
            return this;
        }

        public NotificationDTOBuilder eventType(NotificationEventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public NotificationDTOBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NotificationDTOBuilder message(String message) {
            this.message = message;
            return this;
        }

        public NotificationDTOBuilder channel(NotificationChannel channel) {
            this.channel = channel;
            return this;
        }

        public NotificationDTOBuilder status(NotificationStatus status) {
            this.status = status;
            return this;
        }

        public NotificationDTOBuilder referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public NotificationDTOBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public NotificationDTOBuilder bookingId(Long bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public NotificationDTOBuilder centreId(Long centreId) {
            this.centreId = centreId;
            return this;
        }

        public NotificationDTOBuilder queuePositionBefore(Integer queuePositionBefore) {
            this.queuePositionBefore = queuePositionBefore;
            return this;
        }

        public NotificationDTOBuilder queuePositionAfter(Integer queuePositionAfter) {
            this.queuePositionAfter = queuePositionAfter;
            return this;
        }

        public NotificationDTOBuilder estimatedTimeBefore(String estimatedTimeBefore) {
            this.estimatedTimeBefore = estimatedTimeBefore;
            return this;
        }

        public NotificationDTOBuilder estimatedTimeAfter(String estimatedTimeAfter) {
            this.estimatedTimeAfter = estimatedTimeAfter;
            return this;
        }

        public NotificationDTOBuilder providerMessageId(String providerMessageId) {
            this.providerMessageId = providerMessageId;
            return this;
        }

        public NotificationDTOBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public NotificationDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public NotificationDTOBuilder sentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public NotificationDTOBuilder readAt(LocalDateTime readAt) {
            this.readAt = readAt;
            return this;
        }

        public NotificationDTO build() {
            return new NotificationDTO(notificationId, farmerId, eventType, title, message, channel, status, referenceId,
                    phoneNumber, bookingId, centreId, queuePositionBefore, queuePositionAfter, estimatedTimeBefore,
                    estimatedTimeAfter, providerMessageId, errorMessage, createdAt, sentAt, readAt);
        }
    }
}

