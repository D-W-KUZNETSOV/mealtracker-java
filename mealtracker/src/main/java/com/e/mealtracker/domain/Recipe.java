package com.e.mealtracker.domain;

import com.e.mealtracker.entity.User;
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

    // Связь с пользователем вместо хранения username как строки
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MealType category;

    @Column(length = 1000)
    private String description;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private RecipeVisibility visibility = RecipeVisibility.PRIVATE;

    @Column(columnDefinition = "DECIMAL(10,2)", precision = 10, scale = 2)
    private BigDecimal totalCalories;

    @Column(columnDefinition = "DECIMAL(10,2)", precision = 10, scale = 2)
    private BigDecimal totalProteins;

    @Column(columnDefinition = "DECIMAL(10,2)", precision = 10, scale = 2)
    private BigDecimal totalFats;

    @Column(columnDefinition = "DECIMAL(10,2)", precision = 10, scale = 2)
    private BigDecimal totalCarbs;

    // Расчётные поля — не сохраняются в БД
    @Transient
    private BigDecimal caloriesPer100g;
    @Transient
    private BigDecimal proteinPer100g;
    @Transient
    private BigDecimal fatPer100g;
    @Transient
    private BigDecimal carbsPer100g;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
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



