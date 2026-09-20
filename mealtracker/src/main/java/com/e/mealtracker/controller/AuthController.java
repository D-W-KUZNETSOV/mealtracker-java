package com.e.mealtracker.controller;

import com.e.mealtracker.dto.*;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.security.JwtService;
import com.e.mealtracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    /**
     * Аутентифицирует пользователя по логину и паролю.
     * При успехе возвращает JWT-токен в структурированном виде
     * { "token": "...", "type": "Bearer" }.
     */
    @PostMapping("/login")
    @Operation(summary = "Вход для пользователя")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(token, "Bearer"));
    }

    /**
     * Регистрирует нового пользователя.
     * Принимает валидированный RegisterRequest (username, password, email).
     * Возвращает 201 Created с ApiResponse.
     */
    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("SUCCESS", "Пользователь успешно создан"));
    }
    /**
     * Возвращает данные текущего аутентифицированного пользователя.
     * Используется фронтом при загрузке страницы, чтобы узнать, кто залогинен.
     * Требует валидный JWT в заголовке Authorization.
     */
    @GetMapping("/me")
    @Operation(summary = "Получить данные текущего пользователя")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }
}



