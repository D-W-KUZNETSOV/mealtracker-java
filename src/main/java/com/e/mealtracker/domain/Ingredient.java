package com.e.mealtracker.domain;

import com.e.mealtracker.util.UnitType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class Ingredient {

    // ✅ константа для базовых ингредиентов
    public static final String SYSTEM_USERNAME = "SYSTEM";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "fats_per100g")
    private Double fatsPer100g;

    @Column(name = "proteins_per100g")
    private Double proteinsPer100g;

    @Column(name = "carbs_per100g")
    private Double carbsPer100g;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_type", length = 20)
    private UnitType unitType = UnitType.GRAM;

    @Column(name = "unit_weight_grams")
    private Double unitWeightGrams;

    @Column(name = "calories_per100g")
    private Double caloriesPer100g;

    @Column(name = "category", length = 50)
    private String category = "OTHER";

    public double calculateCaloriesPer100g() {
        // Если калории заданы явно — используем их
        if (caloriesPer100g != null && caloriesPer100g > 0) {
            return caloriesPer100g;
        }
        // Иначе считаем из БЖУ
        double fats = (fatsPer100g != null) ? fatsPer100g : 0.0;
        double proteins = (proteinsPer100g != null) ? proteinsPer100g : 0.0;
        double carbs = (carbsPer100g != null) ? carbsPer100g : 0.0;
        return (fats * 9.0) + (proteins * 4.0) + (carbs * 4.0);
    }
    public boolean isBase() {
        return SYSTEM_USERNAME.equals(username);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


