package com.e.mealtracker.service;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.domain.MealType;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeIngredient;
import com.e.mealtracker.dto.IngredientWeightDto;
import com.e.mealtracker.dto.RecipeDto;
import com.e.mealtracker.repository.IngredientRepository;
import com.e.mealtracker.repository.RecipeIngredientRepository;
import com.e.mealtracker.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    public RecipeDto saveRecipe(RecipeDto dto) {
        Recipe recipe = new Recipe();
        recipe.setName(dto.getName());

        // ✅ Самое важное исправление: конвертируем строку в enum
        String categoryString = dto.getCategory() != null ? dto.getCategory().toUpperCase() : "";
        recipe.setCategory(MealType.valueOf(categoryString));

        recipe = recipeRepository.save(recipe);

        double totalCalories = 0.0;

        for (IngredientWeightDto input : dto.getIngredients()) {
            Optional<Ingredient> optionalIngredient = ingredientRepository.findByName(input.getIngredientName());
            if (optionalIngredient.isEmpty()) {
                throw new IllegalArgumentException("Не найден ингредиент: " + input.getIngredientName());
            }
            Ingredient ingredient = optionalIngredient.get();

            double calories = input.getWeightInGrams() * ingredient.getCaloriesPer100g() / 100.0;
            totalCalories += calories;

            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setWeightInGrams(input.getWeightInGrams());
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setRecipe(recipe);
            recipeIngredientRepository.save(recipeIngredient);
        }

        dto.setTotalCalories(totalCalories);
        return dto;
    }


}

