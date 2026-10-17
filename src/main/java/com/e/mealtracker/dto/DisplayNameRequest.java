package com.e.mealtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DisplayNameRequest {
    // зачем: не даём сохранить пустое имя или строку из 500 символов
    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 100, message = "Слишком длинное имя")
    private String displayName;
}
