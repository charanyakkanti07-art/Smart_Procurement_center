package com.smartprocurement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "farmer_id", nullable = false, unique = true)
    private Farmer farmer;

    @Column(name = "app_enabled", nullable = false)
    private boolean appEnabled = true;

    @Column(name = "sms_enabled", nullable = false)
    private boolean smsEnabled = true;

    @Column(name = "voice_enabled", nullable = false)
    private boolean voiceEnabled = true;

    @Column(nullable = false)
    private String language = "Telugu";

    public NotificationPreference() {
    }

    public NotificationPreference(Long id, Farmer farmer, boolean appEnabled, boolean smsEnabled, boolean voiceEnabled, String language) {
        this.id = id;
        this.farmer = farmer;
        this.appEnabled = appEnabled;
        this.smsEnabled = smsEnabled;
        this.voiceEnabled = voiceEnabled;
        this.language = language;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Farmer getFarmer() {
        return farmer;
    }

    public void setFarmer(Farmer farmer) {
        this.farmer = farmer;
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

    public static NotificationPreferenceBuilder builder() {
        return new NotificationPreferenceBuilder();
    }

    public static class NotificationPreferenceBuilder {
        private Long id;
        private Farmer farmer;
        private boolean appEnabled = true;
        private boolean smsEnabled = true;
        private boolean voiceEnabled = true;
        private String language = "Telugu";

        public NotificationPreferenceBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public NotificationPreferenceBuilder farmer(Farmer farmer) {
            this.farmer = farmer;
            return this;
        }

        public NotificationPreferenceBuilder appEnabled(boolean appEnabled) {
            this.appEnabled = appEnabled;
            return this;
        }

        public NotificationPreferenceBuilder smsEnabled(boolean smsEnabled) {
            this.smsEnabled = smsEnabled;
            return this;
        }

        public NotificationPreferenceBuilder voiceEnabled(boolean voiceEnabled) {
            this.voiceEnabled = voiceEnabled;
            return this;
        }

        public NotificationPreferenceBuilder language(String language) {
            this.language = language;
            return this;
        }

        public NotificationPreference build() {
            return new NotificationPreference(id, farmer, appEnabled, smsEnabled, voiceEnabled, language);
        }
    }
}
