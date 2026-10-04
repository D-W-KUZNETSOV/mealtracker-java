package com.e.mealtracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteAccountRequest {

    @NotBlank(message = "Пароль обязателен для подтверждения")
    private String password;
}