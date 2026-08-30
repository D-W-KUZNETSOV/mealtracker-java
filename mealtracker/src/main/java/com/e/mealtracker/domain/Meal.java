package com.e.mealtracker.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "meals")
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
}
