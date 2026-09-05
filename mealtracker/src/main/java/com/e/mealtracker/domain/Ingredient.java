package com.e.mealtracker.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "ingredients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    // Можно оставить как «кешированное» значение, но лучше считать по формуле
    private Double caloriesPer100g;

    private Double fatsPer100g;
    private Double proteinsPer100g;
    private Double carbsPer100g;

    // Удобный метод для расчёта калорий на 100 г по классической формуле
    public double calculateCaloriesPer100g() {
        double fats = (fatsPer100g != null) ? fatsPer100g : 0.0;
        double proteins = (proteinsPer100g != null) ? proteinsPer100g : 0.0;
        double carbs = (carbsPer100g != null) ? carbsPer100g : 0.0;

        return (fats * 9) + (proteins * 4) + (carbs * 4);
    }
}
