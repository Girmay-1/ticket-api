package com.ticketapi.dao;

import com.ticketapi.model.Order;
import com.ticketapi.model.User;
import com.ticketapi.model.Event;
import com.ticketapi.dao.UserDao;
import com.ticketapi.dao.EventDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderDaoImplTest {

    @Autowired
    private OrderDao orderDao;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private EventDao eventDao;
    
    private Long testUserId;
    private Long testEventId;
    
    @BeforeEach
    void setUp() {
        // Create test user
        User testUser = new User("testuser", "test@example.com", "hashedpassword");
        User savedUser = userDao.createUser(testUser);
        testUserId = savedUser.getId();
        
        // Create test event
        Event testEvent = new Event();
        testEvent.setName("Test Event");
        testEvent.setDescription("Test Description");
        testEvent.setDateTime(LocalDateTime.now().plusDays(30));
        testEvent.setVenue("Test Venue");
        testEvent.setTotalTickets(100);
        testEvent.setAvailableTickets(100);
        testEvent.setPrice(BigDecimal.valueOf(50.00)); // Add price field
        Event savedEvent = eventDao.createEvent(testEvent);
        testEventId = savedEvent.getId();
    }

    @Test
    void createOrder_Success() {
        // Given
        Order order = new Order(testUserId, testEventId, "VIP", BigDecimal.valueOf(99.99));

        // When
        Order saved = orderDao.createOrder(order);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("VIP", saved.getTicketType());
        assertEquals(BigDecimal.valueOf(99.99), saved.getPrice());
        assertEquals("PENDING", saved.getStatus());
    }

    @Test
    void getOrderById_Success() {
        // Given
        Order order = new Order(testUserId, testEventId, "STANDARD", BigDecimal.valueOf(49.99));
        Order saved = orderDao.createOrder(order);

        // When
        Order retrieved = orderDao.getOrderById(saved.getId());

        // Then
        assertNotNull(retrieved);
        assertEquals(saved.getId(), retrieved.getId());
        assertEquals("STANDARD", retrieved.getTicketType());
    }

    @Test
    void getOrderById_NotFound() {
        // When
        Order result = orderDao.getOrderById(999L);

        // Then
        assertNull(result);
    }

    @Test
    void getOrdersByUserId_Success() {
        // Given
        Order order1 = new Order(testUserId, testEventId, "VIP", BigDecimal.valueOf(99.99));
        Order order2 = new Order(testUserId, testEventId, "STANDARD", BigDecimal.valueOf(49.99));
        
        orderDao.createOrder(order1);
        orderDao.createOrder(order2);

        // When
        List<Order> orders = orderDao.getOrdersByUserId(testUserId);

        // Then
        assertNotNull(orders);
        assertEquals(2, orders.size());
    }

    @Test
    void updateOrderWithPaymentIntent_Success() {
        // Given
        Order order = new Order(testUserId, testEventId, "VIP", BigDecimal.valueOf(99.99));
        Order saved = orderDao.createOrder(order);

        // When
        orderDao.updateOrderWithPaymentIntent(saved.getId(), "pi_test123");

        // Then
        Order updated = orderDao.getOrderById(saved.getId());
        assertEquals("pi_test123", updated.getPaymentIntentId());
    }

    @Test
    void updateOrderWithPaymentIntent_OrderNotFound() {
        // When & Then
        assertThrows(RuntimeException.class, () -> 
            orderDao.updateOrderWithPaymentIntent(999L, "pi_test123"));
    }

    @Test
    void updateOrderStatus_Success() {
        // Given
        Order order = new Order(testUserId, testEventId, "VIP", BigDecimal.valueOf(99.99));
        Order saved = orderDao.createOrder(order);

        // When
        orderDao.updateOrderStatus(saved.getId(), "COMPLETED");

        // Then
        Order updated = orderDao.getOrderById(saved.getId());
        assertEquals("COMPLETED", updated.getStatus());
    }

    @Test
    void getOrderByPaymentIntent_Success() {
        // Given
        Order order = new Order(testUserId, testEventId, "VIP", BigDecimal.valueOf(99.99));
        Order saved = orderDao.createOrder(order);
        orderDao.updateOrderWithPaymentIntent(saved.getId(), "pi_unique123");

        // When
        Order found = orderDao.getOrderByPaymentIntent("pi_unique123");

        // Then
        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("pi_unique123", found.getPaymentIntentId());
    }

    @Test
    void getOrderByPaymentIntent_NotFound() {
        // When
        Order result = orderDao.getOrderByPaymentIntent("nonexistent");

        // Then
        assertNull(result);
    }

    @Test
    void deleteOrder_Success() {
        // Given
        Order order = new Order(testUserId, testEventId, "VIP", BigDecimal.valueOf(99.99));
        Order saved = orderDao.createOrder(order);

        // When
        orderDao.deleteOrder(saved.getId());

        // Then
        Order deleted = orderDao.getOrderById(saved.getId());
        assertNull(deleted);
    }
}
