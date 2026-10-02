package com.e.mealtracker.service;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeIngredient;
import com.e.mealtracker.domain.RecipeVisibility;
import com.e.mealtracker.dto.RecipeSummaryDto;
import com.e.mealtracker.dto.RecipeSummaryDto.IngredientItemDto;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.repository.RecipeRepository;
import com.e.mealtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для расчёта КБЖУ рецепта и получения сводки.
 *
 * Используется:
 *  - StatsService — при добавлении порции в дневник (Per100g, calculatePer100g)
 *  - RecipeController — при получении сводки рецепта (getRecipeSummary)
 *  - MealPlanService — при добавлении рецепта в меню (Per100g, calculatePer100g)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeNutritionService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    // ============================================================
    // Record для расчётов на 100 г
    // ============================================================
    public record Per100g(
            BigDecimal calories,
            BigDecimal protein,
            BigDecimal fat,
            BigDecimal carbs
    ) {}

    // ============================================================
    // Сводка рецепта (для RecipeController)
    // ============================================================
    @Transactional(readOnly = true)
    public RecipeSummaryDto getRecipeSummary(Long recipeId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        Recipe recipe = recipeRepository.findById(recipeId)
                .filter(r -> r.getVisibility() == RecipeVisibility.PUBLIC
                        || r.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Рецепт с ID " + recipeId + " не найден или недоступен"));

        List<IngredientItemDto> items = new ArrayList<>();

        for (RecipeIngredient ri : recipe.getIngredients()) {
            Ingredient ing = ri.getIngredient();
            if (ing == null) {
                log.warn("У RecipeIngredient ID {} отсутствует Ingredient", ri.getId());
                continue;
            }

            double weight = ri.getWeightInGrams();
            double factor = weight / 100.0;

            double calsPer100 = ing.calculateCaloriesPer100g();
            double fatsPer100 = safe(ing.getFatsPer100g());
            double protPer100 = safe(ing.getProteinsPer100g());
            double carbsPer100 = safe(ing.getCarbsPer100g());

            IngredientItemDto item = new IngredientItemDto();
            item.setIngredientId(ing.getId());
            item.setName(ing.getName());
            item.setQuantityGrams(weight);
            item.setCaloriesPer100g(calsPer100);
            item.setFatsPer100g(fatsPer100);
            item.setProteinsPer100g(protPer100);
            item.setCarbsPer100g(carbsPer100);
            item.setItemCalories(calsPer100 * factor);

            items.add(item);
        }

        RecipeSummaryDto dto = new RecipeSummaryDto();
        dto.setId(recipe.getId());
        dto.setName(recipe.getName());
        dto.setCategory(recipe.getCategory() != null
                ? recipe.getCategory().getDisplayName()
                : null);
        dto.setDescription(recipe.getDescription());
        dto.setImageUrl(recipe.getImageUrl());
        dto.setVisibility(recipe.getVisibility());
        dto.setIngredients(items);
        dto.setTotalCalories(recipe.getTotalCalories());
        dto.setTotalFats(recipe.getTotalFats());
        dto.setTotalProteins(recipe.getTotalProteins());
        dto.setTotalCarbs(recipe.getTotalCarbs());
        dto.setTotalWeight(recipe.getTotalWeight());
        dto.setServings(recipe.getServings());
        dto.setServingSizeGrams(recipe.getServingSizeGrams());

        return dto;
    }

    // ============================================================
    // Расчёты КБЖУ на 100 г (для StatsService, MealPlanService)
    // ============================================================
    public Per100g calculatePer100g(Recipe recipe) {
        if (recipe == null || recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            return zero();
        }

        BigDecimal totalWeight = recipe.getIngredients().stream()
                .map(ri -> BigDecimal.valueOf(ri.getWeightInGrams()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalWeight.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Recipe {} has zero total weight — per100g = 0", recipe.getId());
            return zero();
        }

        BigDecimal hundred = BigDecimal.valueOf(100);

        return new Per100g(
                safeDivide(recipe.getTotalCalories(), totalWeight, hundred),
                safeDivide(recipe.getTotalProteins(), totalWeight, hundred),
                safeDivide(recipe.getTotalFats(), totalWeight, hundred),
                safeDivide(recipe.getTotalCarbs(), totalWeight, hundred)
        );
    }

    public Per100g calculateForPortion(Per100g per100g, double weightG) {
        if (per100g == null || weightG <= 0) return zero();

        BigDecimal factor = BigDecimal.valueOf(weightG)
                .divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);

        return new Per100g(
                per100g.calories().multiply(factor).setScale(2, RoundingMode.HALF_UP),
                per100g.protein().multiply(factor).setScale(2, RoundingMode.HALF_UP),
                per100g.fat().multiply(factor).setScale(2, RoundingMode.HALF_UP),
                per100g.carbs().multiply(factor).setScale(2, RoundingMode.HALF_UP)
        );
    }

    // ============================================================
    // Вспомогательные методы
    // ============================================================
    private BigDecimal safeDivide(BigDecimal total, BigDecimal weight, BigDecimal hundred) {
        if (total == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return total
                .multiply(hundred)
                .divide(weight, MathContext.DECIMAL32)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private Per100g zero() {
        return new Per100g(
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
        );
    }

    private double safe(Double value) {
        return value != null ? value : 0.0;
    }
}