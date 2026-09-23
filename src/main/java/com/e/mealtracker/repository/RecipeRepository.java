package com.e.mealtracker.repository;

import com.e.mealtracker.domain.MealType;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeVisibility;
import com.e.mealtracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Page<Recipe> findAllByUser(User user, Pageable pageable);

    Page<Recipe> findAllByUserAndCategory(User user, MealType category, Pageable pageable);

    Optional<Recipe> findByIdAndUser(Long id, User user);

    Optional<Recipe> findByNameAndUser(String name, User user);

    List<Recipe> findByVisibility(RecipeVisibility visibility);

    default List<Recipe> findPublicRecipes() {
        return findByVisibility(RecipeVisibility.PUBLIC);
    }
}







