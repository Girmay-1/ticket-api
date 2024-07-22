package com.ticketapi.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Test
    void testGenerateTokenAndExtractUsername() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        assertEquals(username, jwtUtil.extractUsername(token));
    }

    @Test
    void testTokenExpiration() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        assertTrue(jwtUtil.validateToken(token, username));
    }

    @Test
    void testExtractClaim() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        assertEquals(username, jwtUtil.extractClaim(token, Claims::getSubject));
    }
}