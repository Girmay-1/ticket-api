package com.ticketapi.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    private final Key key;
    private static final long DEFAULT_EXPIRATION = 1000 * 60 * 60 * 10; // 10 hours
    private Clock clock = Clock.systemUTC(); // Default to system clock

    public JwtUtil() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    // Method to set a custom clock (for testing)
    void setClock(Clock clock) {
        this.clock = clock;
    }

    public String generateToken(String username) {
        return createToken(username, DEFAULT_EXPIRATION);
    }

    public String generateToken(String username, long expirationMillis) {
        return createToken(username, expirationMillis);
    }

    private String createToken(String subject, long expirationMillis) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(Instant.now(clock)))
                .setExpiration(Date.from(Instant.now(clock).plusMillis(expirationMillis)))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid token", e);
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(Date.from(Instant.now(clock)));
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public String getEncodedKey() {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // Method to create a Key from an encoded string
    public static Key decodeKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }
}