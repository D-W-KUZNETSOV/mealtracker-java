package com.e.mealtracker.repository;

import com.e.mealtracker.domain.MealType;
import com.e.mealtracker.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByCategory(MealType category);
    Optional<Recipe> findByName(String name);

    // Больше ничего не нужно: save, findAll, findById уже есть благодаря JpaRepository
}

