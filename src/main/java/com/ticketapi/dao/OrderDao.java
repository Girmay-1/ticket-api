package com.ticketapi.dao;

import com.ticketapi.model.Order;
import java.util.List;

public interface OrderDao {
    
    /**
     * Create a new order
     */
    Order createOrder(Order order);
    
    /**
     * Get order by ID
     */
    Order getOrderById(Long id);
    
    /**
     * Get all orders for a specific user
     */
    List<Order> getOrdersByUserId(Long userId);
    
    /**
     * Update order with payment intent ID
     */
    void updateOrderWithPaymentIntent(Long orderId, String paymentIntentId);
    
    /**
     * Update order status
     */
    void updateOrderStatus(Long orderId, String status);
    
    /**
     * Get order by payment intent ID
     */
    Order getOrderByPaymentIntent(String paymentIntentId);
    
    /**
     * Delete order by ID
     */
    void deleteOrder(Long id);
}
