package com.e.mealtracker.dto;

import lombok.Data;

@Data
public class DailyStatsDto {
    private final double calories;
    private final double proteins;
    private final double fats;
    private final double carbs;

    public DailyStatsDto(double calories, double proteins, double fats, double carbs) {
        this.calories = Math.round(calories * 10.0) / 10.0;
        this.proteins = Math.round(proteins * 10.0) / 10.0;
        this.fats = Math.round(fats * 10.0) / 10.0;
        this.carbs = Math.round(carbs * 10.0) / 10.0;
    }


    // геттеры
}