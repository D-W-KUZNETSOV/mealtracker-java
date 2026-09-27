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
    private final Double targetProtein;      // может быть null
    private final Double proteinProgressPercent; // может быть null
    private final List<FoodEntryDto> entries; // список записей за день

    // Конструктор для старых вызовов (без целей, без entries)
    public DailyStatsDto(double calories, double proteins, double fats, double carbs) {
        this(calories, proteins, fats, carbs, null, null, Collections.emptyList());
    }

    // Конструктор со всеми полями
    public DailyStatsDto(double calories, double proteins, double fats, double carbs,
                         Double targetProtein, Double proteinProgressPercent,
                         List<FoodEntryDto> entries) {
        this.calories = Math.round(calories * 10.0) / 10.0;
        this.proteins = Math.round(proteins * 10.0) / 10.0;
        this.fats = Math.round(fats * 10.0) / 10.0;
        this.carbs = Math.round(carbs * 10.0) / 10.0;
        this.targetProtein = targetProtein;
        this.proteinProgressPercent = proteinProgressPercent;
        this.entries = entries != null ? entries : Collections.emptyList();
    }
}