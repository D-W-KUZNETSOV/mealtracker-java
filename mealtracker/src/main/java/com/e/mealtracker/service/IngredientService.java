package com.e.mealtracker.service;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.dto.CreateIngredientRequest;
import com.e.mealtracker.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public Ingredient saveIngredient(CreateIngredientRequest request) {
        return ingredientRepository.findByNameIgnoreCase(request.getName())
                .map(existing -> {
                    // Обновляем все поля, если ингредиент уже есть
                    existing.setCaloriesPer100g(request.getCaloriesPer100g());
                    existing.setProteinsPer100g(request.getProteinsPer100g());
                    existing.setFatsPer100g(request.getFatsPer100g());
                    existing.setCarbsPer100g(request.getCarbsPer100g());
                    return ingredientRepository.save(existing);
                })
                .orElseGet(() -> {
                    Ingredient newIngredient = new Ingredient();
                    newIngredient.setName(request.getName());
                    newIngredient.setCaloriesPer100g(request.getCaloriesPer100g());
                    newIngredient.setProteinsPer100g(request.getProteinsPer100g());
                    newIngredient.setFatsPer100g(request.getFatsPer100g());
                    newIngredient.setCarbsPer100g(request.getCarbsPer100g());
                    return ingredientRepository.save(newIngredient);
                });
    }
}
