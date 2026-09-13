package com.e.mealtracker.service;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeIngredient;
import com.e.mealtracker.dto.RecipeSummaryDto;
import com.e.mealtracker.dto.RecipeSummaryDto.IngredientItemDto;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.repository.RecipeRepository;
import com.e.mealtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeNutritionService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public RecipeSummaryDto getRecipeSummary(Long recipeId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        Recipe recipe = recipeRepository.findByIdAndUser(recipeId, user)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Рецепт с ID " + recipeId + " не найден или недоступен пользователю"));

        List<IngredientItemDto> items = new ArrayList<>();

        for (RecipeIngredient ri : recipe.getIngredients()) {
            Ingredient ing = ri.getIngredient();
            if (ing == null) {
                log.warn("У RecipeIngredient ID {} отсутствует Ingredient", ri.getId());
                continue;
            }

            double weight = ri.getWeightInGrams();
            double factor = weight / 100.0;

            // Используем safe() для защиты от null
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

    // Вспомогательный метод safe()
    private double safe(Double value) {
        return value != null ? value : 0.0;
    }
}
