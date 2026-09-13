package com.e.mealtracker.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "food_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_log_id", nullable = false)
    private DailyLog dailyLog;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @Column(length = 200, nullable = true)
    private String productName; // если без рецепта

    @Column(nullable = false)
    private double weightInGrams;

    // КБЖУ на 100 г — берутся из рецепта или продукта
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal caloriesPer100g;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal proteinPer100g;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal fatPer100g;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal carbsPer100g;

    // посчитанные значения для этой порции (не transient, чтобы можно было быстро суммировать)
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal calories;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal protein;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal fat;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal carbs;
}

