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

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MealType category;

    // Коллекция инициализирована — это правильно
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "recipe")
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    /**
     * Безопасный расчёт калорий:
     * - использует метод из Ingredient, который корректно обрабатывает null
     * - не выбрасывает LazyInitializationException, если коллекция пустая
     * (но если коллекция нужна — она должна быть загружена в сервисе)
     */
    public double calculateTotalCalories() {
        double total = 0.0;
        // Если коллекция не загружена (LAZY), этот цикл просто ничего не сделает.
        // Для расчёта в DTO мы всё равно будем брать данные из репозитория,
        // но этот метод полезен для логики внутри транзакции.
        for (RecipeIngredient ri : ingredients) {
            Ingredient ing = ri.getIngredient();
            if (ing != null) {
                // Используем безопасный метод из сущности Ingredient
                double calsPer100 = ing.calculateCaloriesPer100g();
                total += (calsPer100 * ri.getWeightInGrams()) / 100.0;
            }
        }
        return total;
    }
}

