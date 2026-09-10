package com.smartprocurement.dto;

import com.smartprocurement.entity.VoiceEventType;
import java.time.LocalDate;

public class VoiceEventPayloadDTO {
    private Long bookingId;
    private Long farmerId;
    private VoiceEventType event;
    private String reason;
    private LocalDate requestedDate;
    private String requestedTime; // e.g. "10:00 AM – 11:00 AM" or "Morning"
    private String source = "VOICE_AGENT"; // "VOICE_AGENT" or "SIMULATOR"

    public VoiceEventPayloadDTO() {
    }

    public VoiceEventPayloadDTO(Long bookingId, Long farmerId, VoiceEventType event, String reason,
                               LocalDate requestedDate, String requestedTime, String source) {
        this.bookingId = bookingId;
        this.farmerId = farmerId;
        this.event = event;
        this.reason = reason;
        this.requestedDate = requestedDate;
        this.requestedTime = requestedTime;
        this.source = source != null ? source : "VOICE_AGENT";
    }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public VoiceEventType getEvent() { return event; }
    public void setEvent(VoiceEventType event) { this.event = event; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDate getRequestedDate() { return requestedDate; }
    public void setRequestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; }

    public String getRequestedTime() { return requestedTime; }
    public void setRequestedTime(String requestedTime) { this.requestedTime = requestedTime; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public static VoiceEventPayloadDTOBuilder builder() {
        return new VoiceEventPayloadDTOBuilder();
    }

    public static class VoiceEventPayloadDTOBuilder {
        private Long bookingId;
        private Long farmerId;
        private VoiceEventType event;
        private String reason;
        private LocalDate requestedDate;
        private String requestedTime;
        private String source = "VOICE_AGENT";

        public VoiceEventPayloadDTOBuilder bookingId(Long bookingId) { this.bookingId = bookingId; return this; }
        public VoiceEventPayloadDTOBuilder farmerId(Long farmerId) { this.farmerId = farmerId; return this; }
        public VoiceEventPayloadDTOBuilder event(VoiceEventType event) { this.event = event; return this; }
        public VoiceEventPayloadDTOBuilder reason(String reason) { this.reason = reason; return this; }
        public VoiceEventPayloadDTOBuilder requestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; return this; }
        public VoiceEventPayloadDTOBuilder requestedTime(String requestedTime) { this.requestedTime = requestedTime; return this; }
        public VoiceEventPayloadDTOBuilder source(String source) { this.source = source; return this; }

        public VoiceEventPayloadDTO build() {
            return new VoiceEventPayloadDTO(bookingId, farmerId, event, reason, requestedDate, requestedTime, source);
        }
    }
}
