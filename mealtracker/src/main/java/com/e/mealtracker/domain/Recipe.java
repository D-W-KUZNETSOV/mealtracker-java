package com.e.mealtracker.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "username", nullable = false)
    private String username; // ← новое поле

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MealType category;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "recipe")
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    public double calculateTotalCalories() {
        double total = 0.0;
        for (RecipeIngredient ri : ingredients) {
            Ingredient ing = ri.getIngredient();
            if (ing != null) {
                double calsPer100 = ing.calculateCaloriesPer100g();
                total += (calsPer100 * ri.getWeightInGrams()) / 100.0;
            }
        }
        return total;
    }
}
