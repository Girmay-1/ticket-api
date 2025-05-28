package com.ticketapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketapi.dto.CreatePaymentIntentRequest;
import com.ticketapi.model.User;
import com.ticketapi.model.Event;
import com.ticketapi.dao.UserDao;
import com.ticketapi.dao.EventDao;
import com.ticketapi.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
    private String testUsername;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api";
        
        // Create test user with unique username to avoid conflicts
        testUsername = "testuser" + System.currentTimeMillis();
        User testUser = new User(testUsername, "test" + System.currentTimeMillis() + "@example.com", 
                                "$2a$10$N.kmufCB0kSTdA0P8.yQLOmPHHPkRqb3.HcWdDiK1x1R9xf1GtQgm"); // "password" hashed
        User savedUser = userDao.createUser(testUser);
        testUserId = savedUser.getId();
        
        // Create test event
        Event testEvent = new Event();
        testEvent.setName("Test Event " + System.currentTimeMillis());
        testEvent.setDescription("Test Description");
        testEvent.setDateTime(LocalDateTime.now().plusDays(30));
        testEvent.setVenue("Test Venue");
        testEvent.setTotalTickets(100);
        testEvent.setAvailableTickets(100);
        Event savedEvent = eventDao.createEvent(testEvent);
        testEventId = savedEvent.getId();
        
        // Generate JWT token for the test user using the actual username
        jwtToken = jwtUtil.generateToken(testUsername);
        
        // Verify token is not null
        assertNotNull(jwtToken, "JWT token should not be null");
        assertNotNull(testUserId, "Test user ID should not be null");
        assertNotNull(testEventId, "Test event ID should not be null");
    }
    
    @AfterEach
    void cleanup() {
        // Cleanup is handled by H2 database reset between tests
        // Since we're using in-memory database, data is automatically cleaned up
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

        // Then - Should return 200 OK with mock data when Stripe is not configured
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Should contain mock payment intent ID in response
        assertTrue(response.getBody().contains("pi_mock_12345"));
    }
}
