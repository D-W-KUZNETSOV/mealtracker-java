package com.e.mealtracker.service;

import com.e.mealtracker.entity.User;
import com.e.mealtracker.exception.RecipeNotFoundException;
import com.e.mealtracker.repository.UserRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final UserRepository userRepository;

    @Transactional
    public RecipeDto saveRecipe(CreateRecipeRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        Recipe recipe = new Recipe();
        recipe.setName(request.getName());
        recipe.setUser(user);
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

        // Считаем КБЖУ для всего рецепта
        BigDecimal totalCalories = BigDecimal.ZERO;
        BigDecimal totalProteins = BigDecimal.ZERO;
        BigDecimal totalFats = BigDecimal.ZERO;
        BigDecimal totalCarbs = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (IngredientWeightDto input : request.getIngredients()) {
            Ingredient ingredient = ingredientRepository.findById(input.getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException("Ингредиент не найден"));

            BigDecimal weight = BigDecimal.valueOf(input.getWeightInGrams());
            BigDecimal ratio = weight.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

            BigDecimal calsPer100 = BigDecimal.valueOf(ingredient.calculateCaloriesPer100g());
            totalCalories = totalCalories.add(calsPer100.multiply(ratio));
            totalProteins = totalProteins.add(
                    BigDecimal.valueOf(ingredient.getProteinsPer100g()).multiply(ratio)
            );
            totalFats = totalFats.add(
                    BigDecimal.valueOf(ingredient.getFatsPer100g()).multiply(ratio)
            );
            totalCarbs = totalCarbs.add(
                    BigDecimal.valueOf(ingredient.getCarbsPer100g()).multiply(ratio)
            );
            totalWeight = totalWeight.add(weight);
        }

        recipe.setTotalCalories(totalCalories.setScale(2, RoundingMode.HALF_UP));
        recipe.setTotalProteins(totalProteins.setScale(2, RoundingMode.HALF_UP));
        recipe.setTotalFats(totalFats.setScale(2, RoundingMode.HALF_UP));
        recipe.setTotalCarbs(totalCarbs.setScale(2, RoundingMode.HALF_UP));

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

        // Считаем КБЖУ на 100 г
        if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal factor = BigDecimal.valueOf(100).divide(totalWeight, 10, RoundingMode.HALF_UP);

            recipe.setCaloriesPer100g(totalCalories.multiply(factor).setScale(2, RoundingMode.HALF_UP));
            recipe.setProteinPer100g(totalProteins.multiply(factor).setScale(2, RoundingMode.HALF_UP));
            recipe.setFatPer100g(totalFats.multiply(factor).setScale(2, RoundingMode.HALF_UP));
            recipe.setCarbsPer100g(totalCarbs.multiply(factor).setScale(2, RoundingMode.HALF_UP));

            recipeRepository.save(recipe);
        }

        return RecipeDto.fromEntity(recipe);
    }

    @Transactional
    public Recipe getOrCreateRecipe(String recipeName, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        return recipeRepository.findByNameAndUser(recipeName, user)
                .orElseGet(() -> {
                    log.info("Рецепт '{}' не найден для пользователя {}, создаём новый", recipeName, username);
                    Recipe newRecipe = new Recipe();
                    newRecipe.setName(recipeName);
                    newRecipe.setUser(user);
                    return recipeRepository.save(newRecipe);
                });
    }

    @Transactional(readOnly = true)
    public Page<RecipeDto> getAllRecipesByUser(String category, String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        MealType mealType = null;
        if (category != null && !category.isBlank()) {
            try {
                mealType = MealType.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.debug("Некорректная категория: {}", category);
            }
        }

        Page<Recipe> recipesPage;
        if (mealType == null) {
            recipesPage = recipeRepository.findAllByUser(user, pageable);
        } else {
            recipesPage = recipeRepository.findAllByUserAndCategory(user, mealType, pageable);
        }

        return recipesPage.map(RecipeDto::fromEntity);
    }

    @Transactional
    public void deleteRecipeByUser(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        Recipe recipe = recipeRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RecipeNotFoundException(
                        "Рецепт с ID " + id + " не найден или не принадлежит пользователю " + username
                ));
        recipeRepository.delete(recipe);
    }
}





