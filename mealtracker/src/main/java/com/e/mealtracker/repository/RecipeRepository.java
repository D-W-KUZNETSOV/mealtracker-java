package com.e.mealtracker.repository;

import com.e.mealtracker.domain.MealType;
import com.e.mealtracker.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByUsername(String username);

    List<Recipe> findByUsernameAndCategory(String username, MealType category);

    Optional<Recipe> findByIdAndUsername(Long id, String username);

    // Старые методы можно оставить — пригодятся для админки позже
    List<Recipe> findByCategory(MealType category);
    Optional<Recipe> findByName(String name);
    Optional<Recipe> findByNameAndUsername(String name, String username);

}


