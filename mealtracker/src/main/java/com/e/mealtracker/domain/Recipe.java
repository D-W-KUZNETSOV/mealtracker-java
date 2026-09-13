package com.e.mealtracker.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "username", nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MealType category;

    // Эти два поля мы добавляем, чтобы убрать красные строки
    @Column(length = 1000)
    private String description;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Column(columnDefinition = "DECIMAL(10,2)", precision = 10, scale = 2)
    private BigDecimal totalCalories;

    @Column(columnDefinition = "DECIMAL(10,2)", precision = 10, scale = 2)
    private BigDecimal totalProteins;

    @Column(columnDefinition = "DECIMAL(10,2)", precision = 10, scale = 2)
    private BigDecimal totalFats;

    @Column(columnDefinition = "DECIMAL(10,2)", precision = 10, scale = 2)
    private BigDecimal totalCarbs;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "recipe")
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    public BigDecimal calculateTotalCalories() {
        BigDecimal total = BigDecimal.ZERO;
        if (ingredients == null) return total;
        for (RecipeIngredient ri : ingredients) {
            Ingredient ing = ri.getIngredient();
            if (ing == null) continue;
            double calsPer100 = ing.calculateCaloriesPer100g();
            BigDecimal portion = BigDecimal.valueOf(calsPer100)
                    .multiply(BigDecimal.valueOf(ri.getWeightInGrams()))
                    .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
            total = total.add(portion);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalProteins() {
        BigDecimal total = BigDecimal.ZERO;
        if (ingredients == null) return total;
        for (RecipeIngredient ri : ingredients) {
            Ingredient ing = ri.getIngredient();
            if (ing == null || ing.getProteinsPer100g() == null) continue;
            BigDecimal portion = BigDecimal.valueOf(ing.getProteinsPer100g())
                    .multiply(BigDecimal.valueOf(ri.getWeightInGrams()))
                    .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
            total = total.add(portion);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalFats() {
        BigDecimal total = BigDecimal.ZERO;
        if (ingredients == null) return total;
        for (RecipeIngredient ri : ingredients) {
            Ingredient ing = ri.getIngredient();
            if (ing == null || ing.getFatsPer100g() == null) continue;
            BigDecimal portion = BigDecimal.valueOf(ing.getFatsPer100g())
                    .multiply(BigDecimal.valueOf(ri.getWeightInGrams()))
                    .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
            total = total.add(portion);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalCarbs() {
        BigDecimal total = BigDecimal.ZERO;
        if (ingredients == null) return total;
        for (RecipeIngredient ri : ingredients) {
            Ingredient ing = ri.getIngredient();
            if (ing == null || ing.getCarbsPer100g() == null) continue;
            BigDecimal portion = BigDecimal.valueOf(ing.getCarbsPer100g())
                    .multiply(BigDecimal.valueOf(ri.getWeightInGrams()))
                    .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
            total = total.add(portion);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}


