
package com.e.mealtracker.domain; // или entity — главное, чтобы совпадало с остальными

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "recipe")
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    public double calculateTotalCalories() {
        double total = 0.0;
        for (RecipeIngredient ri : ingredients) {
            if (ri.getIngredient() != null) {
                double caloriesPerGram = ri.getIngredient().getCaloriesPer100g() / 100.0;
                total += caloriesPerGram * ri.getWeightInGrams();
            }
        }
        return total;
    }
}
