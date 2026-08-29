package com.e.mealtracker.repository;

import com.e.mealtracker.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    // Больше ничего не нужно: save, findAll, findById уже есть благодаря JpaRepository
}

