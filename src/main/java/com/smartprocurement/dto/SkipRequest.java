package com.smartprocurement.dto;

public class SkipRequest {
    private String reason;

    public SkipRequest() {
    }

    public SkipRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
