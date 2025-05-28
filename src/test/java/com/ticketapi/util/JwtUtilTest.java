package com.ticketapi.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    
    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Set test properties using reflection to avoid Spring context issues
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "MyTestSecretKeyThatIsAtLeast256BitsLongForJWTTestingPurposesAndMeetsSecurityRequirements");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 3600000L);
        ReflectionTestUtils.setField(jwtUtil, "jwtIssuer", "test-issuer");
        // Initialize the JWT utility
        jwtUtil.init();
    }

    @Test
    void testJwtTokenGeneration() {
        String username = "testuser";
        List<String> roles = List.of("USER", "ADMIN");
        
        String token = jwtUtil.generateToken(username, roles);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify we can extract the username
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername);
        
        // Verify we can extract roles
        List<String> extractedRoles = jwtUtil.extractRoles(token);
        assertEquals(roles, extractedRoles);
        
        // Verify token validation
        assertTrue(jwtUtil.validateToken(token, username));
        assertFalse(jwtUtil.validateToken(token, "wronguser"));
    }

    @Test
    void testJwtTokenWithDefaultRole() {
        String username = "testuser";
        
        String token = jwtUtil.generateToken(username);
        
        List<String> extractedRoles = jwtUtil.extractRoles(token);
        assertEquals(List.of("USER"), extractedRoles);
    }
}