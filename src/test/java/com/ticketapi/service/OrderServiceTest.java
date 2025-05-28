package com.ticketapi.service;

import com.ticketapi.dao.OrderDao;
import com.ticketapi.model.Event;
import com.ticketapi.model.Order;
import com.ticketapi.dto.PaymentStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private EventService eventService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderDao, eventService);
    }

    @Test
    void createPendingOrder_Success() {
        // Given
        Long userId = 1L;
        Long eventId = 1L;
        String ticketType = "VIP";
        Double price = 99.99;

        Event mockEvent = new Event();
        mockEvent.setId(eventId);
        mockEvent.setName("Test Event");
        mockEvent.setAvailableTickets(10);

        Order mockOrder = new Order(userId, eventId, ticketType, BigDecimal.valueOf(price));
        mockOrder.setId(1L);

        when(eventService.getEventById(eventId)).thenReturn(mockEvent);
        when(orderDao.createOrder(any(Order.class))).thenReturn(mockOrder);

        // When
        Order result = orderService.createPendingOrder(userId, eventId, ticketType, price);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PENDING", result.getStatus());
        verify(eventService).getEventById(eventId);
        verify(orderDao).createOrder(any(Order.class));
    }

    @Test
    void createPendingOrder_InvalidUserId() {
        assertThrows(IllegalArgumentException.class, () -> 
            orderService.createPendingOrder(null, 1L, "VIP", 99.99));
        
        assertThrows(IllegalArgumentException.class, () -> 
            orderService.createPendingOrder(0L, 1L, "VIP", 99.99));
    }

    @Test
    void createPendingOrder_EventNotFound() {
        // Given
        when(eventService.getEventById(1L)).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            orderService.createPendingOrder(1L, 1L, "VIP", 99.99));
    }

    @Test
    void createPendingOrder_NoTicketsAvailable() {
        // Given
        Event mockEvent = new Event();
        mockEvent.setAvailableTickets(0);
        when(eventService.getEventById(1L)).thenReturn(mockEvent);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            orderService.createPendingOrder(1L, 1L, "VIP", 99.99));
    }

    @Test
    void updateOrderWithPaymentIntent_Success() {
        // When
        orderService.updateOrderWithPaymentIntent(1L, "pi_test123");

        // Then
        verify(orderDao).updateOrderWithPaymentIntent(1L, "pi_test123");
    }

    @Test
    void confirmOrder_Success() {
        // Given
        Order mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setStatus("PENDING");
        when(orderDao.getOrderById(1L)).thenReturn(mockOrder);

        // When
        orderService.confirmOrder(1L);

        // Then
        verify(orderDao).getOrderById(1L);
        verify(orderDao).updateOrderStatus(1L, "COMPLETED");
    }

    @Test
    void confirmOrder_OrderNotFound() {
        // Given
        when(orderDao.getOrderById(1L)).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            orderService.confirmOrder(1L));
    }

    @Test
    void cancelOrder_Success() {
        // Given
        Order mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setStatus("PENDING");
        when(orderDao.getOrderById(1L)).thenReturn(mockOrder);

        // When
        orderService.cancelOrder(1L);

        // Then
        verify(orderDao).updateOrderStatus(1L, "CANCELLED");
    }

    @Test
    void cancelOrder_CompletedOrder() {
        // Given
        Order mockOrder = new Order();
        mockOrder.setStatus("COMPLETED");
        when(orderDao.getOrderById(1L)).thenReturn(mockOrder);

        // When & Then
        assertThrows(IllegalStateException.class, () -> 
            orderService.cancelOrder(1L));
    }

    @Test
    void getUserOrderHistory_Success() {
        // Given
        Order order1 = new Order(1L, 1L, "VIP", BigDecimal.valueOf(99.99));
        order1.setId(1L);
        order1.setPaymentIntentId("pi_test123");
        
        List<Order> mockOrders = Arrays.asList(order1);
        
        Event mockEvent = new Event();
        mockEvent.setName("Test Event");
        
        when(orderDao.getOrdersByUserId(1L)).thenReturn(mockOrders);
        when(eventService.getEventById(1L)).thenReturn(mockEvent);

        // When
        List<PaymentStatusResponse> result = orderService.getUserOrderHistory(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("pi_test123", result.get(0).getPaymentIntentId());
        assertEquals("Test Event", result.get(0).getEventName());
    }

    @Test
    void getOrderById_Success() {
        // Given
        Order mockOrder = new Order();
        mockOrder.setId(1L);
        when(orderDao.getOrderById(1L)).thenReturn(mockOrder);

        // When
        Order result = orderService.getOrderById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getOrderByPaymentIntent_Success() {
        // Given
        Order mockOrder = new Order();
        mockOrder.setPaymentIntentId("pi_test123");
        when(orderDao.getOrderByPaymentIntent("pi_test123")).thenReturn(mockOrder);

        // When
        Order result = orderService.getOrderByPaymentIntent("pi_test123");

        // Then
        assertNotNull(result);
        assertEquals("pi_test123", result.getPaymentIntentId());
    }
}
