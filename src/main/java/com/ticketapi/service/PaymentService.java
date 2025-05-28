package com.ticketapi.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    @Value("${STRIPE_SECRET_KEY:}")
    private String stripeSecretKey;
    
    @PostConstruct
    public void init() {
        if (stripeSecretKey == null || stripeSecretKey.trim().isEmpty()) {
            logger.warn("STRIPE_SECRET_KEY environment variable is not set - Stripe functionality will be disabled");
            return;
        }
        Stripe.apiKey = stripeSecretKey;
        logger.info("Stripe API initialized successfully");
    }
    
    public PaymentIntent createPaymentIntent(long amount, String currency) throws StripeException {
        // Validate input parameters first
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency must be provided");
        }
        
        // Then check if Stripe is configured
        if (stripeSecretKey == null || stripeSecretKey.trim().isEmpty()) {
            throw new IllegalStateException("Stripe is not configured - STRIPE_SECRET_KEY environment variable is missing");
        }
        
        logger.debug("Creating payment intent for amount: {} {}", amount, currency);
        
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency.toLowerCase().trim());
        params.put("automatic_payment_methods", Map.of("enabled", true));

        return PaymentIntent.create(params);
    }
}