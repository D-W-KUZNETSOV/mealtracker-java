package com.e.mealtracker.repository;

import com.e.mealtracker.domain.MealRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealRecipeRepository extends JpaRepository<MealRecipe, Long> {
    // Тут пока ничего дополнительно не нужно: логика проверки дубликатов
    // уже реализована в DataInitializer через countByRecipeAndIngredient-аналог.
    // Если позже понадобится поиск по meal_id или recipe_id — добавим методы.
    java.util.List<MealRecipe> findByMealId(Long mealId);

}

