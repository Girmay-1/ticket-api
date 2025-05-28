package com.ticketapi.service;

import com.ticketapi.dao.OrderDao;
import com.ticketapi.model.Order;
import com.ticketapi.model.Event;
import com.ticketapi.dto.PaymentStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    private final OrderDao orderDao;
    private final EventService eventService;

    public OrderService(OrderDao orderDao, EventService eventService) {
        this.orderDao = orderDao;
        this.eventService = eventService;
    }

    /**
     * Create a pending order for a ticket purchase
     */
    public Order createPendingOrder(Long userId, Long eventId, String ticketType, Double price) {
        logger.info("Creating pending order for user: {}, event: {}, type: {}, price: {}", 
                   userId, eventId, ticketType, price);
        
        // Validate input
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Valid user ID is required");
        }
        if (eventId == null || eventId <= 0) {
            throw new IllegalArgumentException("Valid event ID is required");
        }
        if (ticketType == null || ticketType.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket type is required");
        }
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Valid price is required");
        }

        // Check if event exists and has available tickets
        Event event = eventService.getEventById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found: " + eventId);
        }
        
        if (event.getAvailableTickets() <= 0) {
            throw new IllegalArgumentException("No tickets available for this event");
        }

        // Create the order
        Order order = new Order(userId, eventId, ticketType, BigDecimal.valueOf(price));
        order.setStatus("PENDING");

        Order createdOrder = orderDao.createOrder(order);
        logger.info("Pending order created with ID: {}", createdOrder.getId());
        
        return createdOrder;
    }

    /**
     * Update order with payment intent ID
     */
    public void updateOrderWithPaymentIntent(Long orderId, String paymentIntentId) {
        logger.info("Updating order {} with payment intent: {}", orderId, paymentIntentId);
        
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Valid order ID is required");
        }
        if (paymentIntentId == null || paymentIntentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment intent ID is required");
        }

        orderDao.updateOrderWithPaymentIntent(orderId, paymentIntentId);
    }

    /**
     * Confirm order and update status to COMPLETED
     */
    public void confirmOrder(Long orderId) {
        logger.info("Confirming order: {}", orderId);
        
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Valid order ID is required");
        }

        // Get the order to validate it exists
        Order order = orderDao.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        // Update order status
        orderDao.updateOrderStatus(orderId, "COMPLETED");
        
        // TODO: Create actual ticket record when order is confirmed
        // This would involve creating a Ticket record in the tickets table
        
        logger.info("Order {} confirmed successfully", orderId);
    }

    /**
     * Cancel order and update status to CANCELLED
     */
    public void cancelOrder(Long orderId) {
        logger.info("Cancelling order: {}", orderId);
        
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Valid order ID is required");
        }

        Order order = orderDao.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        if ("COMPLETED".equals(order.getStatus())) {
            throw new IllegalStateException("Cannot cancel completed order");
        }

        orderDao.updateOrderStatus(orderId, "CANCELLED");
        logger.info("Order {} cancelled successfully", orderId);
    }

    /**
     * Get order by ID
     */
    public Order getOrderById(Long orderId) {
        logger.debug("Getting order by ID: {}", orderId);
        
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Valid order ID is required");
        }

        return orderDao.getOrderById(orderId);
    }

    /**
     * Get order by payment intent ID
     */
    public Order getOrderByPaymentIntent(String paymentIntentId) {
        logger.debug("Getting order by payment intent: {}", paymentIntentId);
        
        if (paymentIntentId == null || paymentIntentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment intent ID is required");
        }

        return orderDao.getOrderByPaymentIntent(paymentIntentId);
    }

    /**
     * Get user's order history as PaymentStatusResponse objects
     */
    public List<PaymentStatusResponse> getUserOrderHistory(Long userId) {
        logger.info("Getting order history for user: {}", userId);
        
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Valid user ID is required");
        }

        List<Order> orders = orderDao.getOrdersByUserId(userId);
        
        return orders.stream()
                .map(this::convertOrderToPaymentStatusResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all orders for a user
     */
    public List<Order> getOrdersByUserId(Long userId) {
        logger.debug("Getting orders for user: {}", userId);
        
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Valid user ID is required");
        }

        return orderDao.getOrdersByUserId(userId);
    }

    /**
     * Convert Order to PaymentStatusResponse for API compatibility
     */
    private PaymentStatusResponse convertOrderToPaymentStatusResponse(Order order) {
        PaymentStatusResponse response = new PaymentStatusResponse(
            order.getPaymentIntentId(),
            order.getStatus().toLowerCase(), // Stripe statuses are lowercase
            order.getTotalPrice().multiply(BigDecimal.valueOf(100)).longValue(), // Convert to cents
            "usd",
            order.getId()
        );
        
        response.setTicketType(order.getTicketType());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        
        // Try to get event name
        try {
            Event event = eventService.getEventById(order.getEventId());
            if (event != null) {
                response.setEventName(event.getName());
            }
        } catch (Exception e) {
            logger.warn("Could not fetch event name for order {}: {}", order.getId(), e.getMessage());
        }
        
        return response;
    }
}
