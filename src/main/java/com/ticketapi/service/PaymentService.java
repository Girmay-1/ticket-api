package com.ticketapi.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.ticketapi.dto.CreatePaymentIntentResponse;
import com.ticketapi.dto.PaymentStatusResponse;
import com.ticketapi.model.Event;
import com.ticketapi.model.Order;
import com.ticketapi.model.User;
import com.ticketapi.service.EventService;
import com.ticketapi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    @Value("${stripe.api.key:}")
    private String stripeSecretKey;
    
    private final EventService eventService;
    private final UserService userService;
    private final OrderService orderService;
    
    public PaymentService(EventService eventService, UserService userService, OrderService orderService) {
        this.eventService = eventService;
        this.userService = userService;
        this.orderService = orderService;
    }
    
    @PostConstruct
    public void init() {
        if (stripeSecretKey == null || stripeSecretKey.trim().isEmpty() || stripeSecretKey.equals("default_test_key")) {
            logger.warn("STRIPE_SECRET_KEY environment variable is not set or using default - Stripe functionality will be disabled");
            return;
        }
        Stripe.apiKey = stripeSecretKey;
        logger.info("Stripe API initialized successfully");
    }
    
    public CreatePaymentIntentResponse createPaymentIntentForTicket(String username, Long eventId, 
                                                                   String ticketType, Double price) throws StripeException {
        
        logger.info("Creating payment intent for user: {}, event: {}, type: {}, price: {}", 
                   username, eventId, ticketType, price);
        
        // Input validation
        if (eventId == null || eventId <= 0) {
            throw new IllegalArgumentException("Valid event ID is required");
        }
        
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Valid price is required");
        }
        
        if (ticketType == null || ticketType.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket type is required");
        }
        
        // Get user and event details
        Long userId = userService.getUserIdByUsername(username);
        if (userId == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        
        Event event = eventService.getEventById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found: " + eventId);
        }
        
        // Check if event has available tickets
        if (event.getAvailableTickets() <= 0) {
            throw new IllegalArgumentException("No tickets available for this event");
        }
        
        // Create order first
        Order order = orderService.createPendingOrder(userId, eventId, ticketType, price);
        
        // Convert price to cents for Stripe
        long amountInCents = Math.round(price * 100);
        
        // Check if Stripe is configured - if not, use mock mode for development
        if (stripeSecretKey == null || stripeSecretKey.trim().isEmpty() || 
            stripeSecretKey.equals("default_test_key") || stripeSecretKey.startsWith("default_")) {
            logger.warn("Stripe not configured - using mock mode for development");
            return createMockPaymentIntent(order, amountInCents);
        }
        
        // Create Stripe PaymentIntent
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amountInCents);
        params.put("currency", "usd");
        params.put("automatic_payment_methods", Map.of("enabled", true));
        
        // Add metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("order_id", order.getId().toString());
        metadata.put("user_id", userId.toString());
        metadata.put("event_id", eventId.toString());
        metadata.put("ticket_type", ticketType);
        params.put("metadata", metadata);
        
        PaymentIntent paymentIntent = PaymentIntent.create(params);
        
        // Update order with payment intent ID
        orderService.updateOrderWithPaymentIntent(order.getId(), paymentIntent.getId());
        
        logger.info("Payment intent created successfully: {}", paymentIntent.getId());
        
        return new CreatePaymentIntentResponse(
            paymentIntent.getClientSecret(),
            paymentIntent.getId(),
            amountInCents,
            "usd",
            order.getId()
        );
    }
    
    public void confirmPayment(String paymentIntentId) {
        logger.info("Confirming payment: {}", paymentIntentId);
        
        // Check if Stripe is configured
        if (stripeSecretKey == null || stripeSecretKey.trim().isEmpty() || 
            stripeSecretKey.equals("default_test_key") || stripeSecretKey.startsWith("default_")) {
            logger.warn("Stripe not configured - using mock confirmation for payment: {}", paymentIntentId);
            // For mock mode, just try to find and confirm the order
            Order order = orderService.getOrderByPaymentIntent(paymentIntentId);
            if (order != null) {
                orderService.confirmOrder(order.getId());
                logger.info("Mock payment confirmed and order completed: {}", order.getId());
            }
            return;
        }
        
        try {
            // Get payment intent from Stripe
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            
            if ("succeeded".equals(paymentIntent.getStatus())) {
                // Get order ID from metadata
                String orderIdStr = paymentIntent.getMetadata().get("order_id");
                if (orderIdStr != null) {
                    Long orderId = Long.parseLong(orderIdStr);
                    orderService.confirmOrder(orderId);
                    logger.info("Payment confirmed and order completed: {}", orderId);
                }
            }
        } catch (Exception e) {
            logger.error("Error confirming payment: {}", paymentIntentId, e);
            throw new RuntimeException("Failed to confirm payment", e);
        }
    }
    
    public PaymentStatusResponse getPaymentStatus(String paymentIntentId) {
        logger.info("Getting payment status: {}", paymentIntentId);
        
        // Handle mock payment intents or when Stripe is not configured
        if (paymentIntentId.startsWith("pi_mock_") || 
            stripeSecretKey == null || stripeSecretKey.trim().isEmpty() || 
            stripeSecretKey.equals("default_test_key") || stripeSecretKey.startsWith("default_")) {
            logger.warn("Using mock mode for payment status: {}", paymentIntentId);
            return new PaymentStatusResponse(
                paymentIntentId,
                "succeeded",
                0L,
                "usd",
                null
            );
        }
        
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            String orderIdStr = paymentIntent.getMetadata().get("order_id");
            
            Long orderId = orderIdStr != null ? Long.parseLong(orderIdStr) : null;
            
            PaymentStatusResponse response = new PaymentStatusResponse(
                paymentIntent.getId(),
                paymentIntent.getStatus(),
                paymentIntent.getAmount(),
                paymentIntent.getCurrency(),
                orderId
            );
            
            // Add additional details if order exists
            if (orderId != null) {
                Order order = orderService.getOrderById(orderId);
                if (order != null) {
                    Event event = eventService.getEventById(order.getEventId());
                    if (event != null) {
                        response.setEventName(event.getName());
                    }
                    response.setTicketType(order.getTicketType());
                }
            }
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error getting payment status: {}", paymentIntentId, e);
            throw new RuntimeException("Failed to get payment status", e);
        }
    }
    
    public List<PaymentStatusResponse> getUserPaymentHistory(String username) {
        logger.info("Getting payment history for user: {}", username);
        
        Long userId = userService.getUserIdByUsername(username);
        if (userId == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        
        return orderService.getUserOrderHistory(userId);
    }
    
    // Legacy method for backward compatibility
    public PaymentIntent createPaymentIntent(long amount, String currency) throws StripeException {
        // Validate input parameters first
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency must be provided");
        }
        
        // Check if Stripe is configured - if not, return null to indicate mock mode
        if (stripeSecretKey == null || stripeSecretKey.trim().isEmpty() || 
            stripeSecretKey.equals("default_test_key") || stripeSecretKey.startsWith("default_")) {
            logger.warn("Stripe is not configured - returning null for mock mode");
            return null;  // Return null instead of throwing exception
        }
        
        logger.debug("Creating payment intent for amount: {} {}", amount, currency);
        
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency.toLowerCase().trim());
        params.put("automatic_payment_methods", Map.of("enabled", true));

        return PaymentIntent.create(params);
    }
    
    /**
     * Create a mock payment intent for development when Stripe is not configured
     */
    private CreatePaymentIntentResponse createMockPaymentIntent(Order order, long amountInCents) {
        logger.info("Creating mock payment intent for development - Order ID: {}", order.getId());
        
        // Generate a mock payment intent ID
        String mockPaymentIntentId = "pi_mock_" + System.currentTimeMillis();
        String mockClientSecret = mockPaymentIntentId + "_secret_mock";
        
        // Update order with mock payment intent ID
        orderService.updateOrderWithPaymentIntent(order.getId(), mockPaymentIntentId);
        
        logger.info("Mock payment intent created: {}", mockPaymentIntentId);
        
        return new CreatePaymentIntentResponse(
            mockClientSecret,
            mockPaymentIntentId,
            amountInCents,
            "usd",
            order.getId()
        );
    }
}