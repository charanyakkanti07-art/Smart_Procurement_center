package com.smartprocurement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "journey_monitoring")
public class JourneyMonitoring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "centre_id", nullable = false)
    private ProcurementCentre centre;

    @Column(name = "procurement_time")
    private LocalDateTime procurementTime;

    @Column(name = "estimated_travel_minutes")
    private int estimatedTravelMinutes = 30;

    @Column(name = "buffer_minutes")
    private int bufferMinutes = 15;

    @Column(name = "automatic_call_time")
    private LocalDateTime automaticCallTime;

    @Column(name = "expected_arrival_time")
    private LocalDateTime expectedArrivalTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "journey_status", nullable = false)
    private JourneyStatus journeyStatus = JourneyStatus.SCHEDULED;

    @Column(name = "failed_call_count")
    private int failedCallCount = 0;

    @Column(name = "last_call_time")
    private LocalDateTime lastCallTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public JourneyMonitoring() {
    }

    public JourneyMonitoring(Long id, Booking booking, Farmer farmer, ProcurementCentre centre,
                             LocalDateTime procurementTime, int estimatedTravelMinutes, int bufferMinutes,
                             LocalDateTime automaticCallTime, LocalDateTime expectedArrivalTime,
                             JourneyStatus journeyStatus, int failedCallCount, LocalDateTime lastCallTime,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.booking = booking;
        this.farmer = farmer;
        this.centre = centre;
        this.procurementTime = procurementTime;
        this.estimatedTravelMinutes = estimatedTravelMinutes;
        this.bufferMinutes = bufferMinutes;
        this.automaticCallTime = automaticCallTime;
        this.expectedArrivalTime = expectedArrivalTime;
        this.journeyStatus = journeyStatus;
        this.failedCallCount = failedCallCount;
        this.lastCallTime = lastCallTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }

    public ProcurementCentre getCentre() { return centre; }
    public void setCentre(ProcurementCentre centre) { this.centre = centre; }

    public LocalDateTime getProcurementTime() { return procurementTime; }
    public void setProcurementTime(LocalDateTime procurementTime) { this.procurementTime = procurementTime; }

    public int getEstimatedTravelMinutes() { return estimatedTravelMinutes; }
    public void setEstimatedTravelMinutes(int estimatedTravelMinutes) { this.estimatedTravelMinutes = estimatedTravelMinutes; }

    public int getBufferMinutes() { return bufferMinutes; }
    public void setBufferMinutes(int bufferMinutes) { this.bufferMinutes = bufferMinutes; }

    public LocalDateTime getAutomaticCallTime() { return automaticCallTime; }
    public void setAutomaticCallTime(LocalDateTime automaticCallTime) { this.automaticCallTime = automaticCallTime; }

    public LocalDateTime getExpectedArrivalTime() { return expectedArrivalTime; }
    public void setExpectedArrivalTime(LocalDateTime expectedArrivalTime) { this.expectedArrivalTime = expectedArrivalTime; }

    public JourneyStatus getJourneyStatus() { return journeyStatus; }
    public void setJourneyStatus(JourneyStatus journeyStatus) { this.journeyStatus = journeyStatus; }

    public int getFailedCallCount() { return failedCallCount; }
    public void setFailedCallCount(int failedCallCount) { this.failedCallCount = failedCallCount; }

    public LocalDateTime getLastCallTime() { return lastCallTime; }
    public void setLastCallTime(LocalDateTime lastCallTime) { this.lastCallTime = lastCallTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static JourneyMonitoringBuilder builder() {
        return new JourneyMonitoringBuilder();
    }

    public static class JourneyMonitoringBuilder {
        private Long id;
        private Booking booking;
        private Farmer farmer;
        private ProcurementCentre centre;
        private LocalDateTime procurementTime;
        private int estimatedTravelMinutes = 30;
        private int bufferMinutes = 15;
        private LocalDateTime automaticCallTime;
        private LocalDateTime expectedArrivalTime;
        private JourneyStatus journeyStatus = JourneyStatus.SCHEDULED;
        private int failedCallCount = 0;
        private LocalDateTime lastCallTime;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public JourneyMonitoringBuilder id(Long id) { this.id = id; return this; }
        public JourneyMonitoringBuilder booking(Booking booking) { this.booking = booking; return this; }
        public JourneyMonitoringBuilder farmer(Farmer farmer) { this.farmer = farmer; return this; }
        public JourneyMonitoringBuilder centre(ProcurementCentre centre) { this.centre = centre; return this; }
        public JourneyMonitoringBuilder procurementTime(LocalDateTime procurementTime) { this.procurementTime = procurementTime; return this; }
        public JourneyMonitoringBuilder estimatedTravelMinutes(int estimatedTravelMinutes) { this.estimatedTravelMinutes = estimatedTravelMinutes; return this; }
        public JourneyMonitoringBuilder bufferMinutes(int bufferMinutes) { this.bufferMinutes = bufferMinutes; return this; }
        public JourneyMonitoringBuilder automaticCallTime(LocalDateTime automaticCallTime) { this.automaticCallTime = automaticCallTime; return this; }
        public JourneyMonitoringBuilder expectedArrivalTime(LocalDateTime expectedArrivalTime) { this.expectedArrivalTime = expectedArrivalTime; return this; }
        public JourneyMonitoringBuilder journeyStatus(JourneyStatus journeyStatus) { this.journeyStatus = journeyStatus; return this; }
        public JourneyMonitoringBuilder failedCallCount(int failedCallCount) { this.failedCallCount = failedCallCount; return this; }
        public JourneyMonitoringBuilder lastCallTime(LocalDateTime lastCallTime) { this.lastCallTime = lastCallTime; return this; }
        public JourneyMonitoringBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public JourneyMonitoringBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public JourneyMonitoring build() {
            return new JourneyMonitoring(id, booking, farmer, centre, procurementTime, estimatedTravelMinutes, bufferMinutes,
                    automaticCallTime, expectedArrivalTime, journeyStatus, failedCallCount, lastCallTime, createdAt, updatedAt);
        }
    }
}
