package com.e.mealtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private Instant timestamp;
    private int status;
    private String error;    // HTTP reason phrase, e.g. "Not Found"
    private String code;     // бизнес-код, e.g. "RECIPE_NOT_FOUND"
    private String message;  // человекочитаемое сообщение
    private String path;     // URI запроса
}

