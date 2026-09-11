package com.smartprocurement.service.provider;

public class SmsResult {
    private final boolean success;
    private final String providerMessageId;
    private final String errorMessage;

    public SmsResult(boolean success, String providerMessageId, String errorMessage) {
        this.success = success;
        this.providerMessageId = providerMessageId;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static SmsResult success(String providerMessageId) {
        return new SmsResult(true, providerMessageId, null);
    }

    public static SmsResult failure(String errorMessage) {
        return new SmsResult(false, null, errorMessage);
    }
}
