package com.e.mealtracker.domain;

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

    public double calculateCaloriesPer100g() {
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


