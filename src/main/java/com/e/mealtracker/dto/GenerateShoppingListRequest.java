package com.e.mealtracker.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class GenerateShoppingListRequest {
    private LocalDate periodStart;
    private LocalDate periodEnd;
}
