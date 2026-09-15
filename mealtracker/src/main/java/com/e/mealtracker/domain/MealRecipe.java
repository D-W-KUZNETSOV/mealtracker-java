package com.e.mealtracker.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "meal_recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"meal", "recipe"})
public class MealRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Meal meal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Recipe recipe;

    @Column(nullable = false)
    private double portionWeightGrams;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MealRecipe other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
