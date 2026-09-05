package com.e.mealtracker.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

// ВАЖНО: нужен именно этот импорт для SecretKey
import javax.crypto.SecretKey;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey signingKey; // Было Key, стало SecretKey

    @PostConstruct
    private void init() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            if (keyBytes.length < 32) {
                log.warn("JWT secret key is shorter than 32 bytes. This is insecure!");
            }
            // Keys.hmacShaKeyFor возвращает SecretKey, просто присваиваем
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            log.error("Invalid JWT secret: not a valid Base64 string. Check application.properties", e);
            throw new IllegalStateException("JWT secret is not valid Base64", e);
        }
    }

    // Теперь метод возвращает SecretKey — это то, что хочет verifyWith()
    private SecretKey getSigningKey() {
        return signingKey;
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 86_400_000L))
                .signWith(getSigningKey()) // signWith тоже отлично принимает SecretKey
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private io.jsonwebtoken.Claims extractAllClaims(String token) {
        // verifyWith теперь получит правильный тип SecretKey
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            var claims = extractAllClaims(token);
            String username = claims.getSubject();
            boolean isExpired = claims.getExpiration().before(new Date());

            return username != null && username.equals(userDetails.getUsername()) && !isExpired;
        } catch (Exception e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Map<String, Object> getAllClaims(String token) {
        var claims = extractAllClaims(token);
        Map<String, Object> result = new HashMap<>();
        result.put("sub", claims.getSubject());
        result.put("exp", claims.getExpiration());
        result.put("iat", claims.getIssuedAt());
        return result;
    }
}



