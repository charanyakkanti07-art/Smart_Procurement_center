package com.smartprocurement.service.provider;

import com.smartprocurement.entity.NotificationEventType;

public interface SmsProvider {
    boolean sendSms(String phone, String message, NotificationEventType eventType);
}
