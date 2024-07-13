package com.ticketapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long DEFAULT_EXPIRATION = 1000 * 60 * 60 * 10; // 10 hours

    // Existing method, unchanged
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, DEFAULT_EXPIRATION);
    }

    // New overloaded method for testing
    public String generateToken(String username, long expirationMillis) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, expirationMillis);
    }

    // Modified createToken method
    private String createToken(Map<String, Object> claims, String subject, long expirationMillis) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key)
                .compact();
    }

    // ... rest of your existing methods ...

}