package com.e.mealtracker.repository;

import com.e.mealtracker.domain.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    long countByRecipeIdAndIngredientId(Long recipeId, Long ingredientId);
    // save, saveAll, findById и т.д. уже доступны
}

