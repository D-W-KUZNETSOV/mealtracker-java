package com.e.mealtracker.service;

import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeIngredient;
import com.e.mealtracker.dto.RecipeSummaryDto;
import com.e.mealtracker.dto.RecipeSummaryDto.IngredientItemDto;
import com.e.mealtracker.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeNutritionService {

    private final RecipeRepository recipeRepository;

    @Transactional(readOnly = true)
    public RecipeSummaryDto getRecipeSummary(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Рецепт с ID " + recipeId + " не найден"));

        List<IngredientItemDto> items = new ArrayList<>();

        for (RecipeIngredient ri : recipe.getIngredients()) {
            var ing = ri.getIngredient();
            if (ing == null) continue;

            double weight = ri.getWeightInGrams();
            double factor = weight / 100.0;

            double calsPer100 = ing.calculateCaloriesPer100g();
            double fatsPer100 = safe(ing.getFatsPer100g());
            double protPer100 = safe(ing.getProteinsPer100g());
            double carbsPer100 = safe(ing.getCarbsPer100g());

            IngredientItemDto item = new IngredientItemDto();
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
        dto.setName(recipe.getName());
        dto.setDescription(recipe.getDescription());
        dto.setImageUrl(recipe.getImageUrl());
        dto.setIngredients(items);
        dto.setTotalCalories(recipe.getTotalCalories());
        dto.setTotalFats(recipe.getTotalFats());
        dto.setTotalProteins(recipe.getTotalProteins());
        dto.setTotalCarbs(recipe.getTotalCarbs());
        return dto;
    }

    private static double safe(Double value) {
        return value != null ? value : 0.0;
    }
}
