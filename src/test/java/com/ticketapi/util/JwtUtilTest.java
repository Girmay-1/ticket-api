package com.ticketapi.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=test-secret-key-for-testing-at-least-256-bits-long",
    "jwt.expiration=3600000",
    "jwt.issuer=test-issuer"
})
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

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