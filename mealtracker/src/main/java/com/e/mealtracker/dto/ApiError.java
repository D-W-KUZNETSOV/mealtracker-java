package com.e.mealtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;

@Data
@AllArgsConstructor
public class ApiError {
    private String error;       // код ошибки, например "INVALID_PORTION_WEIGHT"
    private String message;     // понятное сообщение для пользователя/фронтенда
    private Instant timestamp;  // время ошибки
}


