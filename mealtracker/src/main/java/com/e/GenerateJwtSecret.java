package com.e;

import io.jsonwebtoken.security.Keys;
import java.util.Base64;

public class GenerateJwtSecret {
    public static void main(String[] args) {
        // Генерируем случайный ключ для HS256
        var key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);

        // Кодируем в Base64
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());

        System.out.println("Твой JWT secret (скопируй и вставь в application.properties):");
        System.out.println(base64Key);
    }
}
