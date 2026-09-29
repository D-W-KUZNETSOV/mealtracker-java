package com.e.mealtracker.dto;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
public class DailyStatsDto {
    private final double calories;
    private final double proteins;
    private final double fats;
    private final double carbs;
    private final Double targetCalories;
    private final Double caloriesProgressPercent;
    private final Double targetProtein;
    private final Double proteinProgressPercent;
    private final List<FoodEntryDto> entries;

    // Старый конструктор (для совместимости)
    public DailyStatsDto(double calories, double proteins, double fats, double carbs) {
        this(calories, proteins, fats, carbs, null, null, null, null, Collections.emptyList());
    }

    // Полный конструктор
    public DailyStatsDto(double calories, double proteins, double fats, double carbs,
                         Double targetCalories, Double caloriesProgressPercent,
                         Double targetProtein, Double proteinProgressPercent,
                         List<FoodEntryDto> entries) {
        this.calories = Math.round(calories * 10.0) / 10.0;
        this.proteins = Math.round(proteins * 10.0) / 10.0;
        this.fats = Math.round(fats * 10.0) / 10.0;
        this.carbs = Math.round(carbs * 10.0) / 10.0;
        this.targetCalories = targetCalories;
        this.caloriesProgressPercent = caloriesProgressPercent;
        this.targetProtein = targetProtein;
        this.proteinProgressPercent = proteinProgressPercent;
        this.entries = entries != null ? entries : Collections.emptyList();
    }
}