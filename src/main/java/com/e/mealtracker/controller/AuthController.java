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
import org.springframework.security.core.userdetails.UserDetailsService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserDetailsService userDetailsService;

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
        return ResponseEntity.ok(new AuthResponse(
                token,
                "Bearer",
                userDetails.getUsername(),
                jwtService.getExpirationSeconds()
        ));
    }

    /**
     * Регистрирует нового пользователя и сразу возвращает JWT-токен,
     * чтобы фронт не делал отдельный запрос на логин.
     * Возвращает 201 Created с AuthResponse { token, type }.
     */
    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        userService.registerUser(request);
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(
                        token,
                        "Bearer",
                        userDetails.getUsername(),
                        jwtService.getExpirationSeconds()
                ));
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
    /**
     * Запрос на сброс пароля.
     * Принимает email, отправляет ссылку с токеном.
     * Возвращает 204 всегда (не раскрываем, существует ли email).
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Запрос на сброс пароля (отправка ссылки на email)")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        userService.requestPasswordReset(request.getEmail());
        return ResponseEntity.noContent().build();
    }

    /**
     * Сброс пароля по токену.
     * Принимает токен из письма + новый пароль.
     * Возвращает 204 при успехе, 400 при неверном/истёкшем токене.
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Сброс пароля по токену")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }
}




