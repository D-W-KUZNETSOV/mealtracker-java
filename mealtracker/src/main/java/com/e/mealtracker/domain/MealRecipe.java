package com.e.mealtracker.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "meal_recipes")
public class MealRecipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Meal meal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Recipe recipe;

    // вес порции в граммах — если ты съел не весь рецепт
    @Column(nullable = false)
    private double portionWeightGrams;

    // конструкторы, геттеры, сеттеры
}
