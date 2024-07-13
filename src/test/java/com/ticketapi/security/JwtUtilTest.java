//package com.ticketapi.security;
//
//import io.jsonwebtoken.Claims;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.Date;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class JwtUtilTest {
//
//    private JwtUtil jwtUtil;
//
//    @BeforeEach
//    void setUp() {
//        jwtUtil = new JwtUtil();
//    }
//
//    @Test
//    void generateTokenAndValidate() {
//        String username = "testuser";
//        String token = jwtUtil.generateToken(username);
//
//        assertNotNull(token);
//        assertTrue(jwtUtil.validateToken(token, username));
//    }
//
//    @Test
//    void extractUsername() {
//        String username = "testuser";
//        String token = jwtUtil.generateToken(username);
//
//        assertEquals(username, jwtUtil.extractUsername(token));
//    }
//
//    @Test
//    void tokenExpirationDate() {
//        String username = "testuser";
//        String token = jwtUtil.generateToken(username);
//
//        Date expirationDate = jwtUtil.extractExpiration(token);
//        Date now = new Date();
//
//        assertTrue(expirationDate.after(now));
//        // Assuming token is set to expire in 10 hours
//        long tenHoursInMillis = TimeUnit.HOURS.toMillis(10);
//        assertTrue(expirationDate.getTime() - now.getTime() <= tenHoursInMillis);
//    }
//
//    @Test
//    void validateTokenWithWrongUsername() {
//        String username = "testuser";
//        String token = jwtUtil.generateToken(username);
//
//        assertFalse(jwtUtil.validateToken(token, "wronguser"));
//    }
//
//    @Test
//    void validateExpiredToken() throws InterruptedException {
//        String username = "testuser";
//        // You might need to modify JwtUtil to accept expiration time for testing
//        String token = jwtUtil.generateToken(username, 1); // 1 millisecond expiration
//
//        // Wait for token to expire
//        Thread.sleep(2);
//
//        assertFalse(jwtUtil.validateToken(token, username));
//    }
//}