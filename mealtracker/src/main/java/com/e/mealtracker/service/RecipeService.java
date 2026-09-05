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
    public RecipeDto saveRecipe(CreateRecipeRequest request, String username) {
        Recipe recipe = new Recipe();
        recipe.setName(request.getName());
        recipe.setUsername(username);

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
        List<String> missingNutritionalData = new ArrayList<>();
        Map<String, Ingredient> ingredientMap = new HashMap<>();

        // КЛЮЧЕВОЕ ИЗМЕНЕНИЕ: ищем ингредиенты только текущего пользователя
        for (IngredientWeightDto input : request.getIngredients()) {
            String name = input.getIngredientName();
            Optional<Ingredient> optional = ingredientRepository
                    .findByNameIgnoreCaseAndUsername(name, username);

            if (optional.isEmpty()) {
                missingIngredients.add(name);
            } else {
                Ingredient ingredient = optional.get();
                ingredientMap.put(name, ingredient);

                if (ingredient.getCaloriesPer100g() == null
                        || ingredient.getProteinsPer100g() == null
                        || ingredient.getFatsPer100g() == null
                        || ingredient.getCarbsPer100g() == null) {
                    missingNutritionalData.add(ingredient.getName());
                }
            }
        }

        if (!missingIngredients.isEmpty()) {
            throw new IllegalArgumentException(
                    "Следующие ингредиенты не найдены в базе. Сначала добавьте их: " +
                            String.join(", ", missingIngredients)
            );
        }

        if (!missingNutritionalData.isEmpty()) {
            throw new IllegalArgumentException(
                    "У следующих ингредиентов не заполнены данные КБЖУ. Сначала обновите их: " +
                            String.join(", ", missingNutritionalData)
            );
        }

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

        return RecipeDto.fromEntity(recipe);
    }

    @Transactional(readOnly = true)
    public List<RecipeDto> getAllRecipesByUser(String category, String username) {
        List<Recipe> recipes;
        if (category == null || category.isBlank()) {
            recipes = recipeRepository.findByUsername(username);
        } else {
            try {
                MealType mealType = MealType.valueOf(category.toUpperCase());
                recipes = recipeRepository.findByUsernameAndCategory(username, mealType);
            } catch (IllegalArgumentException e) {
                log.debug("Некорректная категория для поиска: {}", category);
                return List.of();
            }
        }
        return recipes.stream().map(RecipeDto::fromEntity).toList();
    }

    @Transactional
    public void deleteRecipeByUser(Long id, String username) {
        Recipe recipe = recipeRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Рецепт с ID " + id + " не найден или не принадлежит пользователю " + username
                ));
        recipeRepository.delete(recipe);
    }


}

