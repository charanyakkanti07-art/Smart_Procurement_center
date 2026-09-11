package com.smartprocurement.config;

import com.smartprocurement.service.provider.MockSmsProvider;
import com.smartprocurement.service.provider.SmsProvider;
import com.smartprocurement.service.provider.TwilioSmsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SmsProviderConfig {

    @Value("${sms.provider:twilio}")
    private String smsProvider;

    @Bean
    @Primary
    public SmsProvider primarySmsProvider(TwilioSmsProvider twilioSmsProvider, MockSmsProvider mockSmsProvider) {
        if ("mock".equalsIgnoreCase(smsProvider)) {
            return mockSmsProvider;
        }
        return twilioSmsProvider;
    }
}
