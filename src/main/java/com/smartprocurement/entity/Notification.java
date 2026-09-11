package com.smartprocurement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private NotificationEventType eventType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "centre_id")
    private Long centreId;

    @Column(name = "queue_position_before")
    private Integer queuePositionBefore;

    @Column(name = "queue_position_after")
    private Integer queuePositionAfter;

    @Column(name = "estimated_time_before")
    private String estimatedTimeBefore;

    @Column(name = "estimated_time_after")
    private String estimatedTimeAfter;

    @Column(name = "provider_message_id")
    private String providerMessageId;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public Notification() {
    }

    public Notification(Long notificationId, Farmer farmer, NotificationEventType eventType, String title,
                        String message, NotificationChannel channel, NotificationStatus status,
                        String referenceId, String phoneNumber, Long bookingId, Long centreId,
                        Integer queuePositionBefore, Integer queuePositionAfter, String estimatedTimeBefore,
                        String estimatedTimeAfter, String providerMessageId, String errorMessage,
                        LocalDateTime createdAt, LocalDateTime sentAt, LocalDateTime readAt) {
        this.notificationId = notificationId;
        this.farmer = farmer;
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

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Farmer getFarmer() {
        return farmer;
    }

    public void setFarmer(Farmer farmer) {
        this.farmer = farmer;
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

    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public static class NotificationBuilder {
        private Long notificationId;
        private Farmer farmer;
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

        public NotificationBuilder notificationId(Long notificationId) {
            this.notificationId = notificationId;
            return this;
        }

        public NotificationBuilder farmer(Farmer farmer) {
            this.farmer = farmer;
            return this;
        }

        public NotificationBuilder eventType(NotificationEventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public NotificationBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NotificationBuilder message(String message) {
            this.message = message;
            return this;
        }

        public NotificationBuilder channel(NotificationChannel channel) {
            this.channel = channel;
            return this;
        }

        public NotificationBuilder status(NotificationStatus status) {
            this.status = status;
            return this;
        }

        public NotificationBuilder referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public NotificationBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public NotificationBuilder bookingId(Long bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public NotificationBuilder centreId(Long centreId) {
            this.centreId = centreId;
            return this;
        }

        public NotificationBuilder queuePositionBefore(Integer queuePositionBefore) {
            this.queuePositionBefore = queuePositionBefore;
            return this;
        }

        public NotificationBuilder queuePositionAfter(Integer queuePositionAfter) {
            this.queuePositionAfter = queuePositionAfter;
            return this;
        }

        public NotificationBuilder estimatedTimeBefore(String estimatedTimeBefore) {
            this.estimatedTimeBefore = estimatedTimeBefore;
            return this;
        }

        public NotificationBuilder estimatedTimeAfter(String estimatedTimeAfter) {
            this.estimatedTimeAfter = estimatedTimeAfter;
            return this;
        }

        public NotificationBuilder providerMessageId(String providerMessageId) {
            this.providerMessageId = providerMessageId;
            return this;
        }

        public NotificationBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public NotificationBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public NotificationBuilder sentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public NotificationBuilder readAt(LocalDateTime readAt) {
            this.readAt = readAt;
            return this;
        }

        public Notification build() {
            return new Notification(notificationId, farmer, eventType, title, message, channel, status, referenceId,
                    phoneNumber, bookingId, centreId, queuePositionBefore, queuePositionAfter, estimatedTimeBefore,
                    estimatedTimeAfter, providerMessageId, errorMessage, createdAt, sentAt, readAt);
        }
    }
}

