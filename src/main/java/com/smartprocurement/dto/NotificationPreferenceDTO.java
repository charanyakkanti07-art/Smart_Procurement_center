package com.smartprocurement.dto;

public class NotificationPreferenceDTO {
    private Long farmerId;
    private boolean appEnabled = true;
    private boolean smsEnabled = true;
    private boolean voiceEnabled = true;
    private String language = "Telugu";

    public NotificationPreferenceDTO() {
    }

    public NotificationPreferenceDTO(Long farmerId, boolean appEnabled, boolean smsEnabled, boolean voiceEnabled, String language) {
        this.farmerId = farmerId;
        this.appEnabled = appEnabled;
        this.smsEnabled = smsEnabled;
        this.voiceEnabled = voiceEnabled;
        this.language = language;
    }

    public Long getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(Long farmerId) {
        this.farmerId = farmerId;
    }

    public boolean isAppEnabled() {
        return appEnabled;
    }

    public void setAppEnabled(boolean appEnabled) {
        this.appEnabled = appEnabled;
    }

    public boolean isSmsEnabled() {
        return smsEnabled;
    }

    public void setSmsEnabled(boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public boolean isVoiceEnabled() {
        return voiceEnabled;
    }

    public void setVoiceEnabled(boolean voiceEnabled) {
        this.voiceEnabled = voiceEnabled;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public static NotificationPreferenceDTOBuilder builder() {
        return new NotificationPreferenceDTOBuilder();
    }

    public static class NotificationPreferenceDTOBuilder {
        private Long farmerId;
        private boolean appEnabled = true;
        private boolean smsEnabled = true;
        private boolean voiceEnabled = true;
        private String language = "Telugu";

        public NotificationPreferenceDTOBuilder farmerId(Long farmerId) {
            this.farmerId = farmerId;
            return this;
        }

        public NotificationPreferenceDTOBuilder appEnabled(boolean appEnabled) {
            this.appEnabled = appEnabled;
            return this;
        }

        public NotificationPreferenceDTOBuilder smsEnabled(boolean smsEnabled) {
            this.smsEnabled = smsEnabled;
            return this;
        }

        public NotificationPreferenceDTOBuilder voiceEnabled(boolean voiceEnabled) {
            this.voiceEnabled = voiceEnabled;
            return this;
        }

        public NotificationPreferenceDTOBuilder language(String language) {
            this.language = language;
            return this;
        }

        public NotificationPreferenceDTO build() {
            return new NotificationPreferenceDTO(farmerId, appEnabled, smsEnabled, voiceEnabled, language);
        }
    }
}
