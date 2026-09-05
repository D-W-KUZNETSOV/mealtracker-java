package com.e.mealtracker.service;

import lombok.extern.slf4j.Slf4j;
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

import java.util.*;

@Slf4j
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

        // Обработка категории
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            String categoryString = request.getCategory().trim().toUpperCase();
            try {
                recipe.setCategory(MealType.valueOf(categoryString));
            } catch (IllegalArgumentException e) {
                log.warn("Некорректная категория '{}', устанавливаем null", categoryString);
                recipe.setCategory(null);
            }
        } else {
            recipe.setCategory(null);
        }

        List<String> missingIngredients = new ArrayList<>();
        List<String> missingNutritionalData = new ArrayList<>(); // <-- добавили этот список
        Map<String, Ingredient> ingredientMap = new HashMap<>();

        for (IngredientWeightDto input : request.getIngredients()) {
            String name = input.getIngredientName();
            Optional<Ingredient> optional = ingredientRepository.findByNameIgnoreCase(name);

            if (optional.isEmpty()) {
                missingIngredients.add(name);
            } else {
                Ingredient ingredient = optional.get();
                ingredientMap.put(name, ingredient);

                // --- ВОТ СЮДА ВСТАВЛЯЕМ ПРОВЕРКУ ---
                if (ingredient.getCaloriesPer100g() == null
                        || ingredient.getProteinsPer100g() == null
                        || ingredient.getFatsPer100g() == null
                        || ingredient.getCarbsPer100g() == null) {
                    missingNutritionalData.add(ingredient.getName());
                }
                // ----------------------------------
            }
        }

        // Сначала проверяем отсутствие ингредиентов
        if (!missingIngredients.isEmpty()) {
            throw new IllegalArgumentException(
                    "Следующие ингредиенты не найдены в базе. Сначала добавьте их: " +
                            String.join(", ", missingIngredients)
            );
        }

        // Потом проверяем неполные данные КБЖУ
        if (!missingNutritionalData.isEmpty()) {
            throw new IllegalArgumentException(
                    "У следующих ингредиентов не заполнены данные КБЖУ. Сначала обновите их: " +
                            String.join(", ", missingNutritionalData)
            );
        }

        // Теперь можно безопасно сохранять рецепт
        recipe = recipeRepository.save(recipe);

        for (IngredientWeightDto input : request.getIngredients()) {
            Ingredient ingredient = ingredientMap.get(input.getIngredientName());

            RecipeIngredient ri = new RecipeIngredient();
            ri.setWeightInGrams(input.getWeightInGrams());
            ri.setIngredient(ingredient);
            ri.setRecipe(recipe);

            recipeIngredientRepository.save(ri);
            recipe.getIngredients().add(ri);
        }

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
                log.debug("Некорректная категория для поиска: {}", category);
                return List.of();
            }
        }
        return recipes.stream().map(this::toDto).toList();
    }

    private RecipeDto toDto(Recipe recipe) {
        RecipeDto dto = new RecipeDto();
        dto.setName(recipe.getName());
        dto.setCategory(recipe.getCategory() == null ? null : recipe.getCategory().name());

        double totalCalories = 0.0;
        List<RecipeIngredientDto> ingredientDtos = new ArrayList<>();

        for (RecipeIngredient ri : recipe.getIngredients()) {
            double calsPer100 = ri.getIngredient().calculateCaloriesPer100g();
            double calories = ri.getWeightInGrams() * calsPer100 / 100.0;
            totalCalories += calories;

            ingredientDtos.add(new RecipeIngredientDto(
                    ri.getIngredient().getName(),
                    ri.getWeightInGrams()
            ));
        }

        dto.setTotalCalories(Math.round(totalCalories));
        dto.setIngredients(ingredientDtos);
        return dto;
    }

    @Transactional
    public void deleteRecipe(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new IllegalArgumentException("Рецепт с ID " + id + " не найден");
        }
        recipeRepository.deleteById(id);
    }
}


