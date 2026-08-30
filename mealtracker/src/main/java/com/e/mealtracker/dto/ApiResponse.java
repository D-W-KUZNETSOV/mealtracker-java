package com.e.mealtracker.dto;

import lombok.Data;

@Data
public class ApiResponse {
    private final String status;
    private final String message;

    public ApiResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}

