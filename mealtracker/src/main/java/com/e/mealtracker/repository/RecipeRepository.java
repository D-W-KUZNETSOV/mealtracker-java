package com.e.mealtracker.repository;

import com.e.mealtracker.domain.DailyLog;
import com.e.mealtracker.domain.MealType;
import com.e.mealtracker.domain.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    // Базовые методы для изоляции по пользователю (самое важное для продакшена)
    List<Recipe> findByUsername(String username);
    Page<Recipe> findByUsername(String username, Pageable pageable);

    // Фильтрация по пользователю + категория (удобно для фильтров в UI)
    List<Recipe> findByUsernameAndCategory(String username, MealType category);

    // Проверка доступа к конкретной записи (чтобы нельзя было редактировать чужой рецепт)
    Optional<Recipe> findByIdAndUsername(Long id, String username);

    // Поиск по имени с привязкой к пользователю (чтобы пользователь видел только свои рецепты с таким именем)
    Optional<Recipe> findByNameAndUsername(String name, String username);

    // Публичные/админские методы (оставляем, но используем осторожно)
    List<Recipe> findByCategory(MealType category);
    Optional<Recipe> findByName(String name);

}



