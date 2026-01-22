package com.enterprise.iam_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtils {
    
    // Requirement: Secure signing key [cite: 5, 80]
    // In production, use a long string (32+ chars) from application.properties
    private final String jwtSecret = "your-very-secure-and-very-long-secret-key-here-12345";
    private final SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    private final long expirationMs = 3600000; // 1 hour [cite: 82]

    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)                 // Replaces setSubject() [cite: 40]
                .issuedAt(now)                  // Replaces setIssuedAt() [cite: 36]
                .expiration(expiryDate)         // Replaces setExpiration() [cite: 34]
                .signWith(key)                  // Uses the new SecretKey type
                .compact();
    }

    public String extractEmail(String token) {
    return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
}