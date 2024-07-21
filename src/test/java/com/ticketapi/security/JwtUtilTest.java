//package com.ticketapi.security;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.Clock;
//import java.time.Duration;
//import java.time.Instant;
//import java.time.ZoneId;
//import java.util.Date;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class JwtUtilTest {
//
//    private JwtUtil jwtUtil;
//    private Clock fixedClock;
//    @BeforeEach
//    void setUp() {
//        jwtUtil = new JwtUtil();
//        fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
//        jwtUtil.setClock(fixedClock);
//    }
//
//    @Test
//    void generateTokenAndValidate() {
//        String username = "testuser";
//        String token = jwtUtil.generateToken(username);
//
//        assertNotNull(token);
////        assertTrue(jwtUtil.validateToken(token, username));
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
////        assertFalse(jwtUtil.validateToken(token, "wronguser"));
//    }
//
//    @Test
//    void validateExpiredToken() {
//        String username = "testuser";
//        String token = jwtUtil.generateToken(username, 1000); // 1 second expiration
//
//        // Move clock forward by 2 seconds
//        jwtUtil.setClock(Clock.offset(fixedClock, Duration.ofSeconds(2)));
//
////        assertFalse(jwtUtil.validateToken(token, username));
//    }
//}