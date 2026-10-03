package com.e.mealtracker.dto;

import com.e.mealtracker.util.UnitType;
import lombok.Data;

@Data
public class CreateShoppingListItemRequest {
    private Long ingredientId;       // опционально — если выбран из БД
    private String ingredientName;   // обязательно — если вручную введён
    private String category;         // опционально
    private Double quantityGrams;    // для GRAM/ML
    private UnitType unitType;
}