package com.e.mealtracker.service;

import lombok.extern.slf4j.Slf4j;
import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.domain.MealType;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeIngredient;
import com.e.mealtracker.dto.CreateRecipeRequest;
import com.e.mealtracker.dto.IngredientWeightDto;
import com.e.mealtracker.dto.RecipeDto;
import com.e.mealtracker.repository.IngredientRepository;
import com.e.mealtracker.repository.RecipeIngredientRepository;
import com.e.mealtracker.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

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
        recipe.setDescription(request.getDescription());
        recipe.setImageUrl(request.getImageUrl());

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

        // Проверяем все ингредиенты до сохранения рецепта
        List<Long> missingIds = new ArrayList<>();
        List<String> missingNutritionalData = new ArrayList<>();

        for (IngredientWeightDto input : request.getIngredients()) {
            Ingredient ingredient = ingredientRepository.findById(input.getIngredientId())
                    .orElse(null);

            if (ingredient == null) {
                missingIds.add(input.getIngredientId());
            } else if (ingredient.getProteinsPer100g() == null
                    || ingredient.getFatsPer100g() == null
                    || ingredient.getCarbsPer100g() == null) {
                missingNutritionalData.add(ingredient.getName());
            }
        }

        if (!missingIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ингредиенты с ID не найдены: " + missingIds
            );
        }

        if (!missingNutritionalData.isEmpty()) {
            throw new IllegalArgumentException(
                    "У следующих ингредиентов не заполнены данные КБЖУ. Сначала обновите их: " +
                            String.join(", ", missingNutritionalData)
            );
        }

        // Считаем КБЖУ в BigDecimal
        BigDecimal totalCalories = BigDecimal.ZERO;
        BigDecimal totalProteins = BigDecimal.ZERO;
        BigDecimal totalFats = BigDecimal.ZERO;
        BigDecimal totalCarbs = BigDecimal.ZERO;

        for (IngredientWeightDto input : request.getIngredients()) {
            Ingredient ingredient = ingredientRepository.findById(input.getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException("Ингредиент не найден"));

            BigDecimal weight = BigDecimal.valueOf(input.getWeightInGrams());
            BigDecimal ratio = weight.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

            // Калории — через метод (считаются из БЖУ)
            BigDecimal calsPer100 = BigDecimal.valueOf(ingredient.calculateCaloriesPer100g());
            totalCalories = totalCalories.add(calsPer100.multiply(ratio));

            // Белки, жиры, углеводы — напрямую из полей
            totalProteins = totalProteins.add(
                    BigDecimal.valueOf(ingredient.getProteinsPer100g()).multiply(ratio)
            );
            totalFats = totalFats.add(
                    BigDecimal.valueOf(ingredient.getFatsPer100g()).multiply(ratio)
            );
            totalCarbs = totalCarbs.add(
                    BigDecimal.valueOf(ingredient.getCarbsPer100g()).multiply(ratio)
            );
        }

        // Округляем до 2 знаков
        recipe.setTotalCalories(totalCalories.setScale(2, RoundingMode.HALF_UP));
        recipe.setTotalProteins(totalProteins.setScale(2, RoundingMode.HALF_UP));
        recipe.setTotalFats(totalFats.setScale(2, RoundingMode.HALF_UP));
        recipe.setTotalCarbs(totalCarbs.setScale(2, RoundingMode.HALF_UP));

        // Сохраняем рецепт
        recipe = recipeRepository.save(recipe);

        // Создаём RecipeIngredient и привязываем к рецепту
        for (IngredientWeightDto input : request.getIngredients()) {
            Ingredient ingredient = ingredientRepository.findById(input.getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException("Ингредиент не найден"));

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




