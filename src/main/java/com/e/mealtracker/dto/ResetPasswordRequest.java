package com.e.mealtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Токен обязателен")
    private String token;

    @NotBlank(message = "Новый пароль обязателен")
    @Size(min = 6, message = "Минимум 6 символов")
    private String newPassword;
}
