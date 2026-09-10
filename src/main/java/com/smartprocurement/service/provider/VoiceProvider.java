package com.smartprocurement.service.provider;

import com.smartprocurement.entity.NotificationEventType;

public interface VoiceProvider {
    boolean sendVoiceCall(String phone, String script, NotificationEventType eventType);
}
