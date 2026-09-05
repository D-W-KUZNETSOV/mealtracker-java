package com.e.mealtracker.repository;

import com.e.mealtracker.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    // Этот метод Spring сгенерирует сам: SELECT * FROM ingredients WHERE name = ?
    Optional<Ingredient> findByName(String name);

    Optional<Ingredient> findByNameIgnoreCase(String name);


}
