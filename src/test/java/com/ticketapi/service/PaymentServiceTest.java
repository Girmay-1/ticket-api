package com.ticketapi.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void testCreatePaymentIntent_StripeNotConfigured() {
        // Test when STRIPE_SECRET_KEY is not set (common in CI/test environments)
        assertThrows(IllegalStateException.class, () -> {
            paymentService.createPaymentIntent(2000L, "USD");
        });
    }
    
    @Test
    void testCreatePaymentIntent_InvalidAmount() {
        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.createPaymentIntent(0L, "USD"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.createPaymentIntent(-100L, "USD"));
    }
    
    @Test
    void testCreatePaymentIntent_InvalidCurrency() {
        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.createPaymentIntent(2000L, null));
        
        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.createPaymentIntent(2000L, ""));
        
        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.createPaymentIntent(2000L, "   "));
    }
}
