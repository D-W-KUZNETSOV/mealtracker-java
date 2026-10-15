package com.e.mealtracker.dto;

import java.math.BigDecimal;

public record FillerSuggestionDto(
        Long ingredientId,
        String ingredientName,
        String category,
        BigDecimal weightInGrams,
        BigDecimal calories,
        BigDecimal proteins
) {}
