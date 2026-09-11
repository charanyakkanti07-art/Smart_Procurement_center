package com.smartprocurement.service.provider;

import com.smartprocurement.entity.NotificationEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("twilioSmsProvider")
public class TwilioSmsProvider implements SmsProvider {

    private static final Logger logger = LoggerFactory.getLogger(TwilioSmsProvider.class);

    @Value("${sms.enabled:true}")
    private boolean smsEnabled;

    @Value("${twilio.account.sid:}")
    private String accountSid;

    @Value("${twilio.auth.token:}")
    private String authToken;

    @Value("${twilio.from.number:}")
    private String fromNumber;

    @Value("${sms.timeout.seconds:10}")
    private int timeoutSeconds;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public boolean sendSms(String phone, String message, NotificationEventType eventType) {
        SmsResult result = sendSmsDetailed(phone, message, eventType);
        return result.isSuccess();
    }

    @Override
    public SmsResult sendSmsDetailed(String phone, String message, NotificationEventType eventType) {
        if (!smsEnabled) {
            logger.info("SMS Notifications are disabled via configuration (sms.enabled=false). Skipping SMS to {}", phone);
            return SmsResult.failure("SMS disabled by configuration");
        }

        if (accountSid == null || accountSid.trim().isEmpty() ||
            authToken == null || authToken.trim().isEmpty() ||
            fromNumber == null || fromNumber.trim().isEmpty()) {
            logger.warn("Twilio SMS credentials incomplete. AccountSid: {}, FromNumber: {}. SMS skipped.",
                    accountSid != null ? accountSid.substring(0, Math.min(6, accountSid.length())) + "..." : "NULL",
                    fromNumber);
            return SmsResult.failure("Incomplete Twilio credentials");
        }

        String formattedPhone = formatPhoneNumber(phone);

        try {
            String url = "https://api.twilio.com/2010-04-01/Accounts/" + accountSid.trim() + "/Messages.json";

            String formBody = "To=" + URLEncoder.encode(formattedPhone, StandardCharsets.UTF_8)
                    + "&From=" + URLEncoder.encode(fromNumber.trim(), StandardCharsets.UTF_8)
                    + "&Body=" + URLEncoder.encode(message, StandardCharsets.UTF_8);

            String authHeader = "Basic " + Base64.getEncoder().encodeToString((accountSid.trim() + ":" + authToken.trim()).getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(timeoutSeconds > 0 ? timeoutSeconds : 10))
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formBody))
                    .build();

            logger.info("Dispatching Twilio SMS to {} for Event {}", formattedPhone, eventType);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            String responseBody = response.body();

            if (statusCode == 200 || statusCode == 201) {
                String sid = extractJsonValue(responseBody, "sid");
                logger.info("Successfully sent Twilio SMS to {}. Message SID: {}", formattedPhone, sid);
                return SmsResult.success(sid != null ? sid : "TWILIO-OK-" + System.currentTimeMillis());
            } else {
                String errorMsg = extractJsonValue(responseBody, "message");
                if (errorMsg == null) {
                    errorMsg = "HTTP Status " + statusCode;
                }
                logger.error("Twilio SMS API Error (HTTP {}): {} - Recipient: {}", statusCode, errorMsg, formattedPhone);
                return SmsResult.failure("Twilio HTTP " + statusCode + ": " + errorMsg);
            }
        } catch (Exception e) {
            logger.error("Exception occurred while sending Twilio SMS to {}", formattedPhone, e);
            return SmsResult.failure("Twilio dispatch exception: " + e.getMessage());
        }
    }

    private String formatPhoneNumber(String phone) {
        if (phone == null) return "";
        String clean = phone.replaceAll("[^0-9+]", "");
        if (clean.startsWith("+")) {
            return clean;
        }
        if (clean.length() == 10) {
            return "+91" + clean;
        }
        return "+" + clean;
    }

    private String extractJsonValue(String json, String key) {
        if (json == null) return null;
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
