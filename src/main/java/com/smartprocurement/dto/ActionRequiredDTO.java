package com.smartprocurement.dto;

import com.smartprocurement.entity.JourneyStatus;
import com.smartprocurement.entity.VoiceEventType;

import java.time.LocalDateTime;

public class ActionRequiredDTO {
    private Long bookingId;
    private Long farmerId;
    private String farmerName;
    private String farmerPhone;
    private Long centreId;
    private String centreName;
    private String slot;
    private JourneyStatus journeyStatus;
    private VoiceEventType issueType; // e.g. MISSED_EXPECTED_ARRIVAL, FARMER_NOT_REACHABLE
    private String issueDescription;
    private LocalDateTime expectedArrivalTime;
    private LocalDateTime timestamp;

    public ActionRequiredDTO() {
    }

    public ActionRequiredDTO(Long bookingId, Long farmerId, String farmerName, String farmerPhone,
                             Long centreId, String centreName, String slot, JourneyStatus journeyStatus,
                             VoiceEventType issueType, String issueDescription,
                             LocalDateTime expectedArrivalTime, LocalDateTime timestamp) {
        this.bookingId = bookingId;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.farmerPhone = farmerPhone;
        this.centreId = centreId;
        this.centreName = centreName;
        this.slot = slot;
        this.journeyStatus = journeyStatus;
        this.issueType = issueType;
        this.issueDescription = issueDescription;
        this.expectedArrivalTime = expectedArrivalTime;
        this.timestamp = timestamp;
    }

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

    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }

    public JourneyStatus getJourneyStatus() { return journeyStatus; }
    public void setJourneyStatus(JourneyStatus journeyStatus) { this.journeyStatus = journeyStatus; }

    public VoiceEventType getIssueType() { return issueType; }
    public void setIssueType(VoiceEventType issueType) { this.issueType = issueType; }

    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }

    public LocalDateTime getExpectedArrivalTime() { return expectedArrivalTime; }
    public void setExpectedArrivalTime(LocalDateTime expectedArrivalTime) { this.expectedArrivalTime = expectedArrivalTime; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public static ActionRequiredDTOBuilder builder() {
        return new ActionRequiredDTOBuilder();
    }

    public static class ActionRequiredDTOBuilder {
        private Long bookingId;
        private Long farmerId;
        private String farmerName;
        private String farmerPhone;
        private Long centreId;
        private String centreName;
        private String slot;
        private JourneyStatus journeyStatus;
        private VoiceEventType issueType;
        private String issueDescription;
        private LocalDateTime expectedArrivalTime;
        private LocalDateTime timestamp;

        public ActionRequiredDTOBuilder bookingId(Long bookingId) { this.bookingId = bookingId; return this; }
        public ActionRequiredDTOBuilder farmerId(Long farmerId) { this.farmerId = farmerId; return this; }
        public ActionRequiredDTOBuilder farmerName(String farmerName) { this.farmerName = farmerName; return this; }
        public ActionRequiredDTOBuilder farmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; return this; }
        public ActionRequiredDTOBuilder centreId(Long centreId) { this.centreId = centreId; return this; }
        public ActionRequiredDTOBuilder centreName(String centreName) { this.centreName = centreName; return this; }
        public ActionRequiredDTOBuilder slot(String slot) { this.slot = slot; return this; }
        public ActionRequiredDTOBuilder journeyStatus(JourneyStatus journeyStatus) { this.journeyStatus = journeyStatus; return this; }
        public ActionRequiredDTOBuilder issueType(VoiceEventType issueType) { this.issueType = issueType; return this; }
        public ActionRequiredDTOBuilder issueDescription(String issueDescription) { this.issueDescription = issueDescription; return this; }
        public ActionRequiredDTOBuilder expectedArrivalTime(LocalDateTime expectedArrivalTime) { this.expectedArrivalTime = expectedArrivalTime; return this; }
        public ActionRequiredDTOBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public ActionRequiredDTO build() {
            return new ActionRequiredDTO(bookingId, farmerId, farmerName, farmerPhone, centreId, centreName,
                    slot, journeyStatus, issueType, issueDescription, expectedArrivalTime, timestamp);
        }
    }
}
