package com.e.mealtracker.service;

import com.e.mealtracker.domain.*;
import com.e.mealtracker.dto.RecipeResponse;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.exception.RecipeNotFoundException;
import com.e.mealtracker.repository.*;
import lombok.extern.slf4j.Slf4j;
import com.e.mealtracker.dto.CreateRecipeRequest;
import com.e.mealtracker.dto.IngredientWeightDto;
import com.e.mealtracker.dto.RecipeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        // 1. Создаём рецепт без КБЖУ
        Recipe recipe = new Recipe();
        recipe.setName(request.getName());
        recipe.setNameLower(request.getName().toLowerCase());   // 🆕
        recipe.setUser(user);
        recipe.setDescription(request.getDescription());
        recipe.setImageUrl(request.getImageUrl());
        recipe.setSteps(RecipeDto.serializeSteps(request.getSteps()));
        if (request.getServings() != null && request.getServings() > 0) {
            recipe.setServings(request.getServings());
        } else {
            recipe.setServings(1);
        }

        // 2. Категория
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            try {
                recipe.setCategory(MealType.valueOf(request.getCategory().trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Некорректная категория '{}', устанавливаем null", request.getCategory());
            }
        }

        // 3. Загружаем ингредиенты, проверяем КБЖУ, добавляем в recipe.ingredients
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

            RecipeIngredient ri = new RecipeIngredient();
            ri.setRecipe(recipe);
            ri.setIngredient(ingredient);
            ri.setWeightInGrams(input.getWeightInGrams());

            recipe.getIngredients().add(ri);   // ← cascade = ALL сохранит
        }

        if (!missingNutritionalData.isEmpty()) {
            throw new IllegalArgumentException(
                    "У следующих ингредиентов не заполнены данные КБЖУ: " +
                            String.join(", ", missingNutritionalData));
        }

        // 4. Считаем КБЖУ через методы Recipe — единственный источник истины
        recipe.setTotalCalories(recipe.calculateTotalCalories());
        recipe.setTotalProteins(recipe.calculateTotalProteins());
        recipe.setTotalFats(recipe.calculateTotalFats());
        recipe.setTotalCarbs(recipe.calculateTotalCarbs());

        // 5. Считаем КБЖУ на 100 г
        BigDecimal totalWeight = recipe.getIngredients().stream()
                .map(ri -> BigDecimal.valueOf(ri.getWeightInGrams()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal factor = BigDecimal.valueOf(100)
                    .divide(totalWeight, 10, RoundingMode.HALF_UP);

            recipe.setCaloriesPer100g(recipe.getTotalCalories().multiply(factor).setScale(2, RoundingMode.HALF_UP));
            recipe.setProteinPer100g(recipe.getTotalProteins().multiply(factor).setScale(2, RoundingMode.HALF_UP));
            recipe.setFatPer100g(recipe.getTotalFats().multiply(factor).setScale(2, RoundingMode.HALF_UP));
            recipe.setCarbsPer100g(recipe.getTotalCarbs().multiply(factor).setScale(2, RoundingMode.HALF_UP));
        } else {
            recipe.setCaloriesPer100g(BigDecimal.ZERO);
            recipe.setProteinPer100g(BigDecimal.ZERO);
            recipe.setFatPer100g(BigDecimal.ZERO);
            recipe.setCarbsPer100g(BigDecimal.ZERO);
        }

        // 6. ОДИН save() — cascade = ALL сохранит и RecipeIngredient
        recipe = recipeRepository.save(recipe);

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
    public Page<RecipeDto> getAllRecipesByUser(
            String category,
            String query,
            BigDecimal minCalories,
            BigDecimal maxCalories,
            BigDecimal minProtein,
            String username,
            Pageable pageable) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        // Для native query category передаём строкой (enum name)
        String categoryStr = null;
        if (category != null && !category.isBlank()) {
            try {
                MealType.valueOf(category.toUpperCase());
                categoryStr = category.toUpperCase();
            } catch (IllegalArgumentException e) {
                log.debug("Некорректная категория: {}", category);
            }
        }
        if (query != null && !query.isBlank()) {
            query = query.toLowerCase();
        }

        Page<Recipe> recipesPage = recipeRepository.searchRecipesNative(
                user.getId(),
                query == null || query.isBlank() ? null : query.trim(),
                categoryStr,
                minCalories,
                maxCalories,
                minProtein,
                pageable
        );

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

    @Transactional
    public RecipeDto updateRecipe(Long id, CreateRecipeRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        // 1. Находим рецепт и проверяем, что он принадлежит юзеру
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Рецепт с ID " + id + " не найден"));

        if (!recipe.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Нет доступа к рецепту");
        }

        // 2. Обновляем простые поля
        recipe.setName(request.getName());
        recipe.setNameLower(request.getName().toLowerCase());   // 🆕
        recipe.setDescription(request.getDescription());
        recipe.setImageUrl(request.getImageUrl());
        recipe.setSteps(RecipeDto.serializeSteps(request.getSteps()));

        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            try {
                recipe.setCategory(MealType.valueOf(request.getCategory().trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Некорректная категория '{}', устанавливаем null", request.getCategory());
                recipe.setCategory(null);
            }
        } else {
            recipe.setCategory(null);
        }

        // 3. Очищаем старые ингредиенты (orphanRemoval = true удалит их из БД)
        recipe.getIngredients().clear();

        // 4. Добавляем новые ингредиенты
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

            RecipeIngredient ri = new RecipeIngredient();
            ri.setRecipe(recipe);
            ri.setIngredient(ingredient);
            ri.setWeightInGrams(input.getWeightInGrams());

            recipe.getIngredients().add(ri);
        }

        if (!missingNutritionalData.isEmpty()) {
            throw new IllegalArgumentException(
                    "У следующих ингредиентов не заполнены данные КБЖУ: " +
                            String.join(", ", missingNutritionalData));
        }

        // 5. Пересчитываем КБЖУ
        recipe.setTotalCalories(recipe.calculateTotalCalories());
        recipe.setTotalProteins(recipe.calculateTotalProteins());
        recipe.setTotalFats(recipe.calculateTotalFats());
        recipe.setTotalCarbs(recipe.calculateTotalCarbs());

        // 6. Пересчитываем КБЖУ на 100 г
        BigDecimal totalWeight = recipe.getIngredients().stream()
                .map(ri -> BigDecimal.valueOf(ri.getWeightInGrams()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal factor = BigDecimal.valueOf(100)
                    .divide(totalWeight, 10, RoundingMode.HALF_UP);

            recipe.setCaloriesPer100g(recipe.getTotalCalories().multiply(factor).setScale(2, RoundingMode.HALF_UP));
            recipe.setProteinPer100g(recipe.getTotalProteins().multiply(factor).setScale(2, RoundingMode.HALF_UP));
            recipe.setFatPer100g(recipe.getTotalFats().multiply(factor).setScale(2, RoundingMode.HALF_UP));
            recipe.setCarbsPer100g(recipe.getTotalCarbs().multiply(factor).setScale(2, RoundingMode.HALF_UP));
        } else {
            recipe.setCaloriesPer100g(BigDecimal.ZERO);
            recipe.setProteinPer100g(BigDecimal.ZERO);
            recipe.setFatPer100g(BigDecimal.ZERO);
            recipe.setCarbsPer100g(BigDecimal.ZERO);
        }

        recipe = recipeRepository.save(recipe);

        return RecipeDto.fromEntity(recipe);
    }
    /**
     * Копирует чужой публичный рецепт в личную коллекцию текущего пользователя.
     * Копия создаётся с visibility = PRIVATE.
     * Базовые ингредиенты переиспользуются, личные — ищутся у текущего юзера
     * по name_lower, иначе копируются как личные.
     */
    @Transactional
    public RecipeDto copyToMy(Long recipeId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        Recipe source = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException(
                        "Рецепт с ID " + recipeId + " не найден"));

        // 1. Нельзя копировать свой рецепт
        if (source.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Это ваш рецепт — копировать не нужно");
        }

        // 2. Нельзя копировать приватный чужой
        if (source.getVisibility() != RecipeVisibility.PUBLIC) {
            throw new AccessDeniedException("Рецепт приватный и недоступен для копирования");
        }

        // 3. 🆕 Idempotency — если копия уже есть, вернуть её
        Optional<Recipe> existing = recipeRepository
                .findByUserAndOriginalRecipeId(user, source.getId());
        if (existing.isPresent()) {
            log.info("Рецепт '{}' уже скопирован юзером {} (id копии {})",
                    source.getName(), username, existing.get().getId());
            return RecipeDto.fromEntity(existing.get());
        }

        // 4. Создаём копию с базовыми полями
        Recipe copy = new Recipe();
        copy.setName(source.getName());
        copy.setNameLower(source.getName().toLowerCase());
        copy.setOriginalRecipeId(source.getId());   // 🆕 — запоминаем оригинал
        copy.setUser(user);


        // 4. Копируем ингредиенты
        for (RecipeIngredient srcRi : source.getIngredients()) {
            Ingredient resolved = resolveIngredientForUser(srcRi.getIngredient(), username);

            RecipeIngredient newRi = new RecipeIngredient();
            newRi.setRecipe(copy);
            newRi.setIngredient(resolved);
            newRi.setWeightInGrams(srcRi.getWeightInGrams());

            copy.getIngredients().add(newRi);
        }

        // 5. Пересчитываем КБЖУ (как в saveRecipe)
        copy.setTotalCalories(copy.calculateTotalCalories());
        copy.setTotalProteins(copy.calculateTotalProteins());
        copy.setTotalFats(copy.calculateTotalFats());
        copy.setTotalCarbs(copy.calculateTotalCarbs());

        BigDecimal totalWeight = copy.getIngredients().stream()
                .map(ri -> BigDecimal.valueOf(ri.getWeightInGrams()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal factor = BigDecimal.valueOf(100)
                    .divide(totalWeight, 10, RoundingMode.HALF_UP);

            copy.setCaloriesPer100g(copy.getTotalCalories().multiply(factor).setScale(2, RoundingMode.HALF_UP));
            copy.setProteinPer100g(copy.getTotalProteins().multiply(factor).setScale(2, RoundingMode.HALF_UP));
            copy.setFatPer100g(copy.getTotalFats().multiply(factor).setScale(2, RoundingMode.HALF_UP));
            copy.setCarbsPer100g(copy.getTotalCarbs().multiply(factor).setScale(2, RoundingMode.HALF_UP));
        } else {
            copy.setCaloriesPer100g(BigDecimal.ZERO);
            copy.setProteinPer100g(BigDecimal.ZERO);
            copy.setFatPer100g(BigDecimal.ZERO);
            copy.setCarbsPer100g(BigDecimal.ZERO);
        }

        // 6. Один save — cascade сохранит RecipeIngredient
        Recipe saved = recipeRepository.save(copy);
        log.info("Рецепт '{}' скопирован юзером {}", source.getName(), username);
        return RecipeDto.fromEntity(saved);
    }

    /**
     * Возвращает ингредиент, который можно использовать в рецепте текущего юзера.
     * Базовый → как есть.
     * Личный чужой → ищем у текущего по name_lower, если нет — копируем.
     */
    private Ingredient resolveIngredientForUser(Ingredient src, String username) {
        // Базовый — переиспользуем
        if (src.isBase()) {
            return src;
        }

        // Личный — ищем у текущего по name_lower
        return ingredientRepository
                .findByNameLowerAndUsername(src.getNameLower(), username)
                .orElseGet(() -> {
                    Ingredient copy = new Ingredient();
                    copy.setName(src.getName());
                    copy.setUsername(username);
                    copy.setFatsPer100g(src.getFatsPer100g());
                    copy.setProteinsPer100g(src.getProteinsPer100g());
                    copy.setCarbsPer100g(src.getCarbsPer100g());
                    copy.setCaloriesPer100g(src.getCaloriesPer100g());
                    copy.setUnitType(src.getUnitType());
                    copy.setUnitWeightGrams(src.getUnitWeightGrams());
                    copy.setCategory(src.getCategory());
                    // name_lower заполнит @PrePersist
                    return ingredientRepository.save(copy);
                });
    }




}





