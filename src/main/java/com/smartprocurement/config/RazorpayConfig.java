package com.smartprocurement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Value("${razorpay.key.id:rzp_test_default_smart_mandi}")
    private String keyId;

    @Value("${razorpay.key.secret:test_secret_default_smart_mandi}")
    private String keySecret;

    @Value("${razorpay.currency:INR}")
    private String currency;

    @Value("${razorpay.deposit.amount.paise:30000}")
    private Integer depositAmountPaise;

    public String getKeyId() {
        return keyId;
    }

    public String getKeySecret() {
        return keySecret;
    }

    public String getCurrency() {
        return currency;
    }

    public Integer getDepositAmountPaise() {
        return depositAmountPaise;
    }
}
