package com.e.mealtracker.controller;

import com.e.mealtracker.dto.UserResponse;
import com.e.mealtracker.entity.Role;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.security.JwtService;
import com.e.mealtracker.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты AuthController")
class AuthControllerTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private UserService userService;
    @Mock private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("/me: возвращает данные текущего пользователя")
    void shouldReturnCurrentUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("dmitriy");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);

        when(authentication.getName()).thenReturn("dmitriy");
        when(userService.findByUsername("dmitriy")).thenReturn(user);

        ResponseEntity<UserResponse> response = authController.me(authentication);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getUsername()).isEqualTo("dmitriy");
        assertThat(response.getBody().getEmail()).isEqualTo("test@example.com");
        assertThat(response.getBody().getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("/me: 401 если пользователь не найден в БД")
    void shouldPropagateExceptionWhenUserNotFound() {
        when(authentication.getName()).thenReturn("ghost");
        when(userService.findByUsername("ghost"))
                .thenThrow(new IllegalArgumentException("Пользователь не найден: ghost"));

        org.assertj.core.api.Assertions
                .assertThatThrownBy(() -> authController.me(authentication))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ghost");
    }
}