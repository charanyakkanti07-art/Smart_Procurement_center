package com.smartprocurement.service.provider;

import com.smartprocurement.entity.NotificationEventType;

public interface SmsProvider {
    boolean sendSms(String phone, String message, NotificationEventType eventType);

    default SmsResult sendSmsDetailed(String phone, String message, NotificationEventType eventType) {
        boolean ok = sendSms(phone, message, eventType);
        return ok ? SmsResult.success("SMS-REF-" + System.currentTimeMillis()) : SmsResult.failure("SMS send failed");
    }
}

