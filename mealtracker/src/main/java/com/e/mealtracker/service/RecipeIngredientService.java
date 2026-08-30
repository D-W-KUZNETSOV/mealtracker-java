package com.e.mealtracker.service;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeIngredient;
import com.e.mealtracker.repository.IngredientRepository;
import com.e.mealtracker.repository.RecipeIngredientRepository;
import com.e.mealtracker.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecipeIngredientService {

    private final RecipeIngredientRepository recipeIngredientRepo;
    private final IngredientRepository ingredientRepo;
    private final RecipeRepository recipeRepo;

    @Transactional
    public void addIngredientToRecipe(Long recipeId, Long ingredientId, double weightInGrams) {
        // 1. Проверяем существование ингредиента
        Ingredient ingredient = ingredientRepo.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ингредиент с ID " + ingredientId + " не найден"));

        // 2. Проверяем существование рецепта
        Recipe recipe = recipeRepo.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Рецепт с ID " + recipeId + " не найден"));

        // 3. Проверяем, не добавлен ли уже этот ингредиент в рецепт
        long count = recipeIngredientRepo.countByRecipeAndIngredient(recipe, ingredient);
        if (count > 0) {
            throw new IllegalStateException(
                    "Этот ингредиент уже добавлен в рецепт. Используйте PUT для обновления веса.");
        }

        // 4. Создаём связь
        RecipeIngredient ri = new RecipeIngredient();
        ri.setRecipe(recipe);
        ri.setIngredient(ingredient);
        ri.setWeightInGrams(weightInGrams);

        recipeIngredientRepo.save(ri);
    }

    @Transactional
    public void updateWeightInRecipe(Long recipeId, Long ingredientId, double weightInGrams) {
        Ingredient ingredient = ingredientRepo.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ингредиент с ID " + ingredientId + " не найден"));

        Recipe recipe = recipeRepo.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Рецепт с ID " + recipeId + " не найден"));

        var existing = recipeIngredientRepo
                .findByRecipeAndIngredient(recipe, ingredient)
                .orElseThrow(() -> new IllegalStateException(
                        "Связь рецепта и ингредиента не найдена. Сначала добавьте ингредиент через POST."));

        existing.setWeightInGrams(weightInGrams);
        recipeIngredientRepo.save(existing);
    }
}
