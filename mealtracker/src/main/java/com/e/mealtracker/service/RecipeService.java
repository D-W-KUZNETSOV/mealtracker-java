package com.e.mealtracker.service;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.domain.MealType;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeIngredient;
import com.e.mealtracker.dto.CreateRecipeRequest;
import com.e.mealtracker.dto.IngredientWeightDto;
import com.e.mealtracker.dto.RecipeDto;
import com.e.mealtracker.dto.RecipeIngredientDto;
import com.e.mealtracker.repository.IngredientRepository;
import com.e.mealtracker.repository.RecipeIngredientRepository;
import com.e.mealtracker.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    @Transactional
    public RecipeDto saveRecipe(CreateRecipeRequest request) {
        Recipe recipe = new Recipe();
        recipe.setName(request.getName());

        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            String categoryString = request.getCategory().trim().toUpperCase();
            try {
                recipe.setCategory(MealType.valueOf(categoryString));
            } catch (IllegalArgumentException e) {
                recipe.setCategory(null);
            }
        } else {
            recipe.setCategory(null);
        }

        // Сначала сохраняем рецепт, чтобы у него появился ID
        recipe = recipeRepository.save(recipe);

        // Инициализируем коллекцию, если она null (на всякий случай)
        if (recipe.getIngredients() == null) {
            recipe.setIngredients(new java.util.ArrayList<>());
        }

        double totalCalories = 0.0;

        for (IngredientWeightDto input : request.getIngredients()) {
            Optional<Ingredient> optionalIngredient = ingredientRepository.findByNameIgnoreCase(input.getIngredientName()); // лучше ignoreCase
            if (optionalIngredient.isEmpty()) {
                throw new IllegalArgumentException("Не найден ингредиент: " + input.getIngredientName());
            }
            Ingredient ingredient = optionalIngredient.get();

            RecipeIngredient ri = new RecipeIngredient();
            ri.setWeightInGrams(input.getWeightInGrams());
            ri.setIngredient(ingredient);
            ri.setRecipe(recipe);

            // Сохраняем связь
            ri = recipeIngredientRepository.save(ri);

            // Добавляем в коллекцию рецепта (чтобы toDto сразу видел)
            recipe.getIngredients().add(ri);

            // Считаем калории прямо тут
            double calories = ri.getWeightInGrams() * ingredient.getCaloriesPer100g() / 100.0;
            totalCalories += calories;
        }


        recipe = recipeRepository.save(recipe); // обновляем рецепт с totalCalories

        return toDto(recipe);
    }
    public List<RecipeDto> getAllRecipes(String category) {
        List<Recipe> recipes;

        if (category == null) {
            recipes = recipeRepository.findAll();
        } else {
            try {
                MealType mealType = MealType.valueOf(category.toUpperCase());
                recipes = recipeRepository.findByCategory(mealType);
            } catch (IllegalArgumentException e) {
                return List.of(); // пустой список, если категория неверная
            }
        }

        return recipes.stream()
                .map(this::toDto)
                .toList();
    }

    private RecipeDto toDto(Recipe recipe) {
        RecipeDto dto = new RecipeDto();
        dto.setName(recipe.getName());
        dto.setCategory(recipe.getCategory() == null ? null : recipe.getCategory().name());

        double totalCalories = 0.0;
        for (RecipeIngredient ri : recipe.getIngredients()) {
            double calories = ri.getWeightInGrams() * ri.getIngredient().getCaloriesPer100g() / 100.0;
            totalCalories += calories;
        }
        dto.setTotalCalories(totalCalories);

        List<RecipeIngredientDto> ingredientDtos = recipe.getIngredients().stream()
                .map(ri -> new RecipeIngredientDto(
                        ri.getIngredient().getName(),
                        ri.getWeightInGrams()
                ))
                .toList();

        dto.setIngredients(ingredientDtos);
        return dto;
    }
}


