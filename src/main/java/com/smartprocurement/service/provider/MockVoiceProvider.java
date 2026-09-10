package com.smartprocurement.service.provider;

import com.smartprocurement.entity.NotificationEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MockVoiceProvider implements VoiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(MockVoiceProvider.class);

    @Override
    public boolean sendVoiceCall(String phone, String script, NotificationEventType eventType) {
        LocalDateTime timestamp = LocalDateTime.now();
        logger.info("=================== [MOCK VOICE CALL PROVIDER] ===================");
        logger.info("Voice Recipient: {}", phone);
        logger.info("Event Type     : {}", eventType);
        logger.info("Timestamp      : {}", timestamp);
        logger.info("Voice Script   : {}", script);
        logger.info("AI Voice Agent Integration Status: READY");
        logger.info("==================================================================");
        return true;
    }
}
