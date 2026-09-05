package com.e.mealtracker.dto;

import lombok.Data;

@Data
public class DailyStatsDto {
    private final double calories;
    private final double proteins;
    private final double fats;
    private final double carbs;
    private final Double targetProtein;      // может быть null
    private final Double proteinProgressPercent; // может быть null

    // Конструктор для старых вызовов (без целей)
    public DailyStatsDto(double calories, double proteins, double fats, double carbs) {
        this(calories, proteins, fats, carbs, null, null);
    }

    // Новый конструктор со всеми полями
    public DailyStatsDto(double calories, double proteins, double fats, double carbs,
                         Double targetProtein, Double proteinProgressPercent) {
        this.calories = Math.round(calories * 10.0) / 10.0;
        this.proteins = Math.round(proteins * 10.0) / 10.0;
        this.fats = Math.round(fats * 10.0) / 10.0;
        this.carbs = Math.round(carbs * 10.0) / 10.0;
        this.targetProtein = targetProtein;
        this.proteinProgressPercent = proteinProgressPercent;
    }
}
