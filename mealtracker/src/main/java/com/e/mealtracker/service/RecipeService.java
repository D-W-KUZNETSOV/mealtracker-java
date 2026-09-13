package com.e.mealtracker.service;

import com.e.mealtracker.domain.*;
import com.e.mealtracker.dto.RecipeResponse;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.exception.RecipeNotFoundException;
import com.e.mealtracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import com.e.mealtracker.dto.CreateRecipeRequest;
import com.e.mealtracker.dto.IngredientWeightDto;
import com.e.mealtracker.dto.RecipeDto;
import com.e.mealtracker.repository.IngredientRepository;
import com.e.mealtracker.repository.RecipeIngredientRepository;
import com.e.mealtracker.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public RecipeDto saveRecipe(CreateRecipeRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        Recipe recipe = new Recipe();
        recipe.setName(request.getName());
        recipe.setUser(user);
        recipe.setDescription(request.getDescription());
        recipe.setImageUrl(request.getImageUrl());

        // Категория
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            try {
                recipe.setCategory(MealType.valueOf(request.getCategory().trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Некорректная категория '{}', устанавливаем null", request.getCategory());
                recipe.setCategory(null);
            }
        }

        // Загружаем ингредиенты один раз
        Map<Long, Ingredient> ingredientMap = new LinkedHashMap<>();
        List<String> missingNutritionalData = new ArrayList<>();

        for (IngredientWeightDto input : request.getIngredients()) {
            Ingredient ingredient = ingredientRepository.findById(input.getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Ингредиент с ID " + input.getIngredientId() + " не найден"));

            if (ingredient.getProteinsPer100g() == null
                    || ingredient.getFatsPer100g() == null
                    || ingredient.getCarbsPer100g() == null) {
                missingNutritionalData.add(ingredient.getName());
            }
            ingredientMap.put(input.getIngredientId(), ingredient);
        }

        if (!missingNutritionalData.isEmpty()) {
            throw new IllegalArgumentException(
                    "У следующих ингредиентов не заполнены данные КБЖУ: " +
                            String.join(", ", missingNutritionalData));
        }

        // Считаем суммарные КБЖУ и общий вес
        BigDecimal totalCalories = BigDecimal.ZERO;
        BigDecimal totalProteins = BigDecimal.ZERO;
        BigDecimal totalFats = BigDecimal.ZERO;
        BigDecimal totalCarbs = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (IngredientWeightDto input : request.getIngredients()) {
            Ingredient ingredient = ingredientMap.get(input.getIngredientId());
            BigDecimal weight = BigDecimal.valueOf(input.getWeightInGrams());
            BigDecimal ratio = weight.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

            totalCalories = totalCalories.add(
                    BigDecimal.valueOf(ingredient.calculateCaloriesPer100g()).multiply(ratio));
            totalProteins = totalProteins.add(
                    BigDecimal.valueOf(ingredient.getProteinsPer100g()).multiply(ratio));
            totalFats = totalFats.add(
                    BigDecimal.valueOf(ingredient.getFatsPer100g()).multiply(ratio));
            totalCarbs = totalCarbs.add(
                    BigDecimal.valueOf(ingredient.getCarbsPer100g()).multiply(ratio));
            totalWeight = totalWeight.add(weight);
        }

        recipe.setTotalCalories(totalCalories.setScale(2, RoundingMode.HALF_UP));
        recipe.setTotalProteins(totalProteins.setScale(2, RoundingMode.HALF_UP));
        recipe.setTotalFats(totalFats.setScale(2, RoundingMode.HALF_UP));
        recipe.setTotalCarbs(totalCarbs.setScale(2, RoundingMode.HALF_UP));

        // КБЖУ на 100 г
        if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal factor = BigDecimal.valueOf(100)
                    .divide(totalWeight, 10, RoundingMode.HALF_UP);

            recipe.setCaloriesPer100g(totalCalories.multiply(factor).setScale(2, RoundingMode.HALF_UP));
            recipe.setProteinPer100g(totalProteins.multiply(factor).setScale(2, RoundingMode.HALF_UP));
            recipe.setFatPer100g(totalFats.multiply(factor).setScale(2, RoundingMode.HALF_UP));
            recipe.setCarbsPer100g(totalCarbs.multiply(factor).setScale(2, RoundingMode.HALF_UP));
        } else {
            recipe.setCaloriesPer100g(BigDecimal.ZERO);
            recipe.setProteinPer100g(BigDecimal.ZERO);
            recipe.setFatPer100g(BigDecimal.ZERO);
            recipe.setCarbsPer100g(BigDecimal.ZERO);
        }

        // Сохраняем один раз — cascade сохранит и RecipeIngredient
        recipe = recipeRepository.save(recipe);

        for (IngredientWeightDto input : request.getIngredients()) {
            RecipeIngredient ri = new RecipeIngredient();
            ri.setWeightInGrams(input.getWeightInGrams());
            ri.setIngredient(ingredientMap.get(input.getIngredientId()));
            ri.setRecipe(recipe);
            recipe.getIngredients().add(ri);
        }

        recipe = recipeRepository.save(recipe);  // flush с ингредиентами
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


    public List<RecipeResponse> getPublicRecipes() {
        return recipeRepository.findPublicRecipes().stream()
                .map(RecipeResponse::fromRecipe)
                .collect(Collectors.toList());
    }
    @Transactional
    public RecipeResponse toggleRecipeVisibility(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        Recipe recipe = recipeRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RecipeNotFoundException(
                        "Рецепт с ID " + id + " не найден или не принадлежит пользователю " + username
                ));

        if (recipe.getVisibility() == RecipeVisibility.PUBLIC) {
            recipe.setVisibility(RecipeVisibility.PRIVATE);
        } else {
            recipe.setVisibility(RecipeVisibility.PUBLIC);
        }

        recipeRepository.save(recipe);

        return new RecipeResponse(
                recipe.getId(),
                recipe.getName(),
                recipe.getCategory() != null ? recipe.getCategory().getDisplayName() : null,
                recipe.getTotalCalories(),
                recipe.getTotalProteins(),
                recipe.getTotalFats(),
                recipe.getTotalCarbs(),
                recipe.getVisibility()
        );
    }




}





