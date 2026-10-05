package com.e.mealtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FeedbackRequest {

    @NotBlank(message = "Текст обязателен")
    @Size(min = 5, max = 5000, message = "От 5 до 5000 символов")
    private String message;

    @Size(max = 255, message = "Email до 255 символов")
    private String contactEmail;   // опционально
}