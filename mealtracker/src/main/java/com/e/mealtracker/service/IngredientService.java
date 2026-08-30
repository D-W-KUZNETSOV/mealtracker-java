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
                    // Если хочешь, чтобы при повторной отправке калории обновлялись:
                    existing.setCaloriesPer100g(request.getCaloriesPer100g());
                    return ingredientRepository.save(existing);
                })
                .orElseGet(() -> {
                    Ingredient newIngredient = new Ingredient();
                    newIngredient.setName(request.getName());
                    newIngredient.setCaloriesPer100g(request.getCaloriesPer100g());
                    return ingredientRepository.save(newIngredient);
                });
    }
}
