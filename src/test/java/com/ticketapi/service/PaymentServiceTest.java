package com.ticketapi.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.ticketapi.model.Event;
import com.ticketapi.model.Order;
import com.ticketapi.dto.CreatePaymentIntentResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void testCreatePaymentIntent_StripeNotConfigured() {
        // Test when STRIPE_SECRET_KEY is not set (common in CI/test environments)
        // This should now use mock mode instead of throwing exception
        assertDoesNotThrow(() -> {
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

    @Test
    void testCreatePaymentIntentForTicket_InvalidInput() {
        // Test invalid user ID
        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.createPaymentIntentForTicket("testuser", null, "VIP", 99.99));
        
        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.createPaymentIntentForTicket("testuser", 0L, "VIP", 99.99));
        
        // Test invalid price
        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.createPaymentIntentForTicket("testuser", 1L, "VIP", null));
        
        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.createPaymentIntentForTicket("testuser", 1L, "VIP", 0.0));
        
        // Test invalid ticket type
        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.createPaymentIntentForTicket("testuser", 1L, null, 99.99));
        
        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.createPaymentIntentForTicket("testuser", 1L, "", 99.99));
    }

    @Test
    void testCreatePaymentIntentForTicket_UserNotFound() {
        // This will likely throw an exception when trying to find user
        // The exact behavior depends on UserService implementation
        assertThrows(Exception.class, () -> 
            paymentService.createPaymentIntentForTicket("nonexistentuser", 1L, "VIP", 99.99));
    }

    @Test
    void testConfirmPayment_InvalidPaymentIntentId() {
        // Test with invalid payment intent ID
        assertThrows(Exception.class, () -> 
            paymentService.confirmPayment("invalid_payment_intent_id"));
    }

    @Test
    void testGetPaymentStatus_InvalidPaymentIntentId() {
        // Test with invalid payment intent ID
        assertThrows(Exception.class, () -> 
            paymentService.getPaymentStatus("invalid_payment_intent_id"));
    }

    @Test
    void testGetUserPaymentHistory_InvalidUser() {
        // Test with non-existent user
        assertThrows(Exception.class, () -> 
            paymentService.getUserPaymentHistory("nonexistentuser"));
    }
}
