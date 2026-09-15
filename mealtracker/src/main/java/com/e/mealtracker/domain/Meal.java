package com.e.mealtracker.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "meals")
@NoArgsConstructor
@ToString(exclude = "mealRecipes")
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType type; // BREAKFAST, LUNCH, DINNER, SNACK

    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealRecipe> mealRecipes = new ArrayList<>();

    // конструкторы, геттеры, сеттеры
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Meal other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
