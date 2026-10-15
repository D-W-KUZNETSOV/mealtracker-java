package com.e.mealtracker.dto;

import java.math.BigDecimal;
import java.util.List;

public record FillSuggestionsResponse(
        BigDecimal caloriesNeeded,
        BigDecimal proteinsNeeded,
        List<FillerSuggestionDto> suggestions
) {}