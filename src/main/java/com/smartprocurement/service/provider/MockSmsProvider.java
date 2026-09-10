package com.smartprocurement.service.provider;

import com.smartprocurement.entity.NotificationEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MockSmsProvider implements SmsProvider {

    private static final Logger logger = LoggerFactory.getLogger(MockSmsProvider.class);

    @Override
    public boolean sendSms(String phone, String message, NotificationEventType eventType) {
        LocalDateTime timestamp = LocalDateTime.now();
        logger.info("==================== [MOCK SMS PROVIDER] ====================");
        logger.info("SMS Recipient : {}", phone);
        logger.info("Event Type    : {}", eventType);
        logger.info("Timestamp     : {}", timestamp);
        logger.info("Message       : {}", message);
        logger.info("=============================================================");
        return true;
    }
}
