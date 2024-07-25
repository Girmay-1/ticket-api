package com.ticketapi.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.ticketapi.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody Map<String, Object> paymentInfo, Authentication authentication) {
        String username = authentication.getName();
        try {
            PaymentIntent intent = paymentService.createPaymentIntent(
                    ((Number) paymentInfo.get("amount")).longValue(),
                    (String) paymentInfo.get("currency")
            );
            Map<String, String> response = Map.of("clientSecret", intent.getClientSecret());
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}