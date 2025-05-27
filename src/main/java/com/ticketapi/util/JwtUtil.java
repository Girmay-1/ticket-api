package com.ticketapi.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    @Value("${jwt.issuer}")
    private String jwtIssuer;
    
    private Key key;
    private Clock clock = Clock.systemUTC(); // Default to system clock

    @PostConstruct
    public void init() {
        // Use configured secret instead of random key
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Method to set a custom clock (for testing)
    void setClock(Clock clock) {
        this.clock = clock;
    }

    public String generateToken(String username) {
        return generateToken(username, List.of("USER")); // Default role
    }

    public String generateToken(String username, List<String> roles) {
        return createToken(username, roles, jwtExpiration);
    }

    public String generateToken(String username, List<String> roles, long expirationMillis) {
        return createToken(username, roles, expirationMillis);
    }

    private String createToken(String subject, List<String> roles, long expirationMillis) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuer(jwtIssuer)
                .claim("roles", roles)
                .setIssuedAt(Date.from(Instant.now(clock)))
                .setExpiration(Date.from(Instant.now(clock).plusMillis(expirationMillis)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
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

    // Method to create a Key from an encoded string (for testing)
    public static Key decodeKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }
}