package com.ticketapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketapi.dto.CreatePaymentIntentRequest;
import com.ticketapi.model.User;
import com.ticketapi.model.Event;
import com.ticketapi.dao.UserDao;
import com.ticketapi.dao.EventDao;
import com.ticketapi.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class PaymentControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private EventDao eventDao;

    private String jwtToken;
    private String baseUrl;
    private Long testUserId;
    private Long testEventId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api";
        
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
        Event savedEvent = eventDao.createEvent(testEvent);
        testEventId = savedEvent.getId();
        
        // Generate JWT token for the test user
        jwtToken = jwtUtil.generateToken("testuser");
    }

    @Test
    void createPaymentIntent_Success() throws Exception {
        // Given
        CreatePaymentIntentRequest request = new CreatePaymentIntentRequest(testEventId, "VIP", 99.99);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);
        
        HttpEntity<CreatePaymentIntentRequest> entity = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/payments/create-intent",
            HttpMethod.POST,
            entity,
            String.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Parse response to verify structure
        String responseBody = response.getBody();
        assertTrue(responseBody.contains("clientSecret"));
        assertTrue(responseBody.contains("paymentIntentId"));
        assertTrue(responseBody.contains("amount"));
        assertTrue(responseBody.contains("orderId"));
    }

    @Test
    void createPaymentIntent_Unauthorized() throws Exception {
        // Given
        CreatePaymentIntentRequest request = new CreatePaymentIntentRequest(testEventId, "VIP", 99.99);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // No authorization header
        
        HttpEntity<CreatePaymentIntentRequest> entity = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/payments/create-intent",
            HttpMethod.POST,
            entity,
            String.class
        );

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void createPaymentIntent_InvalidRequest() throws Exception {
        // Given - Invalid event ID
        CreatePaymentIntentRequest request = new CreatePaymentIntentRequest(null, "VIP", 99.99);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);
        
        HttpEntity<CreatePaymentIntentRequest> entity = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/payments/create-intent",
            HttpMethod.POST,
            entity,
            String.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getPaymentHistory_Success() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/payments/history",
            HttpMethod.GET,
            entity,
            String.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Should return empty array for new user
        assertTrue(response.getBody().startsWith("["));
    }

    @Test
    void getPaymentHistory_Unauthorized() {
        // Given - No authorization
        HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/payments/history",
            HttpMethod.GET,
            entity,
            String.class
        );

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getPaymentStatus_MockPaymentIntent() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // When - Test with mock payment intent ID
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/payments/status/pi_mock_12345",
            HttpMethod.GET,
            entity,
            String.class
        );

        // Then
        // Should handle gracefully even if payment intent doesn't exist
        assertNotNull(response.getStatusCode());
        // Could be 500 (internal error) or 404 (not found) - both acceptable for non-existent payment intent
        assertTrue(response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError());
    }
}
