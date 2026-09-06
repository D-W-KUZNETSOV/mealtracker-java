package com.e.mealtracker.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "recipe_ingredients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Владелец связи со стороны рецепта: именно тут будет колонка recipe_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    // Владелец связи со стороны ингредиента: тут будет колонка ingredient_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    // Вес ингредиента именно в этом рецепте (в граммах)
    @Column(nullable = false)
    private double weightInGrams;

    public Ingredient getIngredient() {
        return ingredient;
    }

    public double getWeightInGrams() {
        return weightInGrams;
    }
}
