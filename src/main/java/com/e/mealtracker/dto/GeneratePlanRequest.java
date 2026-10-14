package com.e.mealtracker.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GeneratePlanRequest {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
}
