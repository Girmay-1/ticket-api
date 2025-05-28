package com.ticketapi.controller;

import com.ticketapi.dto.CreatePaymentIntentRequest;
import com.ticketapi.dto.CreatePaymentIntentResponse;
import com.ticketapi.dto.PaymentStatusResponse;
import com.ticketapi.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Payment processing operations")
@SecurityRequirement(name = "jwt_auth")
public class PaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    
    private final PaymentService paymentService;
    
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @PostMapping("/create-intent")
    @Operation(summary = "Create payment intent for ticket purchase")
    public ResponseEntity<?> createPaymentIntent(
            @RequestBody CreatePaymentIntentRequest request,
            Authentication authentication) {
        
        try {
            logger.info("Creating payment intent for user: {}, eventId: {}", 
                       authentication.getName(), request.getEventId());
            
            CreatePaymentIntentResponse response = paymentService.createPaymentIntentForTicket(
                authentication.getName(), 
                request.getEventId(),
                request.getTicketType(),
                request.getPrice()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid payment request: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
            
        } catch (Exception e) {
            logger.error("Error creating payment intent", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing payment request");
        }
    }
    
    @GetMapping("/status/{paymentIntentId}")
    @Operation(summary = "Get payment status")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String paymentIntentId) {
        
        try {
            PaymentStatusResponse status = paymentService.getPaymentStatus(paymentIntentId);
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            logger.error("Error getting payment status for: {}", paymentIntentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving payment status");
        }
    }
    
    @GetMapping("/history")
    @Operation(summary = "Get user's payment history")
    public ResponseEntity<?> getPaymentHistory(Authentication authentication) {
        
        try {
            var history = paymentService.getUserPaymentHistory(authentication.getName());
            return ResponseEntity.ok(history);
            
        } catch (Exception e) {
            logger.error("Error getting payment history for user: {}", authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving payment history");
        }
    }
    
    @PostMapping("/confirm")
    @Operation(summary = "Confirm payment completion (internal use)")
    public ResponseEntity<String> confirmPayment(@RequestParam String paymentIntentId) {
        
        try {
            paymentService.confirmPayment(paymentIntentId);
            return ResponseEntity.ok("Payment confirmed successfully");
            
        } catch (Exception e) {
            logger.error("Error confirming payment: {}", paymentIntentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error confirming payment");
        }
    }
}