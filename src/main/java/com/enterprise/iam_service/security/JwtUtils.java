package com.enterprise.iam_service.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value; // Import this!
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    
    // Grabs the 64-character string from application.properties
    @Value("${application.security.jwt.secret-key}")
    private String jwtSecret;
    
    // Grabs the 86400000 (24h) value from application.properties
    @Value("${application.security.jwt.expiration}")
    private long expirationMs; 

    // Helper method to turn the String secret into a cryptographic Key object
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey()) // Uses the secure key
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Verifies signature before reading
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}