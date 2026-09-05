package com.e.mealtracker.repository;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {

    // Считаем, есть ли уже связь между конкретным рецептом и ингредиентом
    long countByRecipeAndIngredient(Recipe recipe, Ingredient ingredient);

    // Ищем существующую связь
    Optional<RecipeIngredient> findByRecipeAndIngredient(Recipe recipe, Ingredient ingredient);
    List<RecipeIngredient> findAllByRecipeId(Long recipeId);

    void deleteByIngredientId(Long ingredientId);
    long countByRecipe(Recipe recipe);
}


