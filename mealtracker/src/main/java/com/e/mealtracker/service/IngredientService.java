package com.e.mealtracker.service;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.dto.CreateIngredientRequest;
import com.e.mealtracker.dto.IngredientResponseDto;
import com.e.mealtracker.dto.IngredientUpdateDTO;
import com.e.mealtracker.exception.ResourceNotFoundException;
import com.e.mealtracker.repository.IngredientRepository;
import com.e.mealtracker.repository.RecipeIngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    public boolean existsById(Long id, String username) {
        return ingredientRepository.existsByIdAndUsername(id, username);
    }

    public Ingredient saveIngredient(CreateIngredientRequest request, String username) {
        return ingredientRepository.findByNameIgnoreCaseAndUsername(request.getName(), username)
                .map(existing -> {
                    existing.setCaloriesPer100g(request.getCaloriesPer100g());
                    existing.setProteinsPer100g(request.getProteinsPer100g());
                    existing.setFatsPer100g(request.getFatsPer100g());
                    existing.setCarbsPer100g(request.getCarbsPer100g());
                    return ingredientRepository.save(existing);
                })
                .orElseGet(() -> {
                    Ingredient newIngredient = new Ingredient();
                    newIngredient.setName(request.getName());
                    newIngredient.setUsername(username);
                    newIngredient.setCaloriesPer100g(request.getCaloriesPer100g());
                    newIngredient.setProteinsPer100g(request.getProteinsPer100g());
                    newIngredient.setFatsPer100g(request.getFatsPer100g());
                    newIngredient.setCarbsPer100g(request.getCarbsPer100g());
                    return ingredientRepository.save(newIngredient);
                });
    }

    @Transactional(readOnly = true)
    public List<Ingredient> findAllByUsername(String username) {
        return ingredientRepository.findAllByUsername(username);
    }

    public IngredientResponseDto updateById(Long id, IngredientUpdateDTO dto, String username) {
        Ingredient ingredient = ingredientRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ингредиент с id=" + id + " не найден или не принадлежит пользователю " + username
                ));

        if (dto.getName() != null) {
            ingredient.setName(dto.getName());
        }
        if (dto.getFatsPer100g() != null) {
            ingredient.setFatsPer100g(dto.getFatsPer100g());
        }
        if (dto.getProteinsPer100g() != null) {
            ingredient.setProteinsPer100g(dto.getProteinsPer100g());
        }
        if (dto.getCarbsPer100g() != null) {
            ingredient.setCarbsPer100g(dto.getCarbsPer100g());
        }

        ingredient.setCaloriesPer100g(ingredient.calculateCaloriesPer100g());

        Ingredient saved = ingredientRepository.save(ingredient);
        return toResponseDto(saved);
    }

    public void deleteById(Long id, String username) {
        Ingredient ingredient = ingredientRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ингредиент с id=" + id + " не найден или не принадлежит пользователю " + username
                ));
        recipeIngredientRepository.deleteByIngredientId(id);
        ingredientRepository.delete(ingredient);
    }

    private IngredientResponseDto toResponseDto(Ingredient ingredient) {
        IngredientResponseDto dto = new IngredientResponseDto();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setFatsPer100g(ingredient.getFatsPer100g());
        dto.setProteinsPer100g(ingredient.getProteinsPer100g());
        dto.setCarbsPer100g(ingredient.getCarbsPer100g());
        dto.setCaloriesPer100g(ingredient.getCaloriesPer100g());
        return dto;
    }
}

