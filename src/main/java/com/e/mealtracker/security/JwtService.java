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

    @Value("${jwt.expiration-ms:86400000}")
    private long expirationMs;   // 24 часа по умолчанию

    private SecretKey signingKey; // Было Key, стало SecretKey

    @PostConstruct
    private void init() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64URL.decode(secret);
        } catch (Exception e1) {
            log.warn("JWT secret is not base64url, falling back to BASE64");
            try {
                keyBytes = Decoders.BASE64.decode(secret);
            } catch (Exception e2) {
                throw new IllegalStateException("JWT secret is invalid", e2);
            }
        }
        if (keyBytes.length < 32) {
            log.warn("JWT secret key is shorter than 32 bytes. This is insecure!");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // Теперь метод возвращает SecretKey — это то, что хочет verifyWith()
    private SecretKey getSigningKey() {
        return signingKey;
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
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
    /**
     * Возвращает время жизни токена в секундах.
     * Используется фронтом, чтобы знать, когда токен истечёт.
     */
    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }
}



