package com.e.mealtracker.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateMealPlanRequest {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
}
