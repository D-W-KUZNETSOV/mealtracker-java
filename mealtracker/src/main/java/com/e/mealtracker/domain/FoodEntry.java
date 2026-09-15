package com.e.mealtracker.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "food_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"dailyLog", "recipe"})

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

    @Column(name = "product_name", length = 200)
    private String productName;

    @Column(name = "weight_in_grams", nullable = false)
    private double weightInGrams;

    @Column(name = "calories_per100g", precision = 10, scale = 2, nullable = false)
    private BigDecimal caloriesPer100g;

    @Column(name = "protein_per100g", precision = 10, scale = 2, nullable = false)
    private BigDecimal proteinPer100g;

    @Column(name = "fat_per100g", precision = 10, scale = 2, nullable = false)
    private BigDecimal fatPer100g;

    @Column(name = "carbs_per100g", precision = 10, scale = 2, nullable = false)
    private BigDecimal carbsPer100g;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal calories;

    // ✅ БЫЛО: private BigDecimal protein;
    @Column(name = "proteins", precision = 10, scale = 2, nullable = false)
    private BigDecimal protein;

    // ✅ БЫЛО: private BigDecimal fat;
    @Column(name = "fats", precision = 10, scale = 2, nullable = false)
    private BigDecimal fat;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal carbs;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoodEntry other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

