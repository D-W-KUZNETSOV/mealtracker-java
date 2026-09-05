package com.e.mealtracker.repository;

import com.e.mealtracker.domain.Ingredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    Optional<Ingredient> findByNameIgnoreCaseAndUsername(String name, String username);

    List<Ingredient> findAllByUsername(String username);

    boolean existsByIdAndUsername(Long id, String username);

    Optional<Ingredient> findByIdAndUsername(Long id, String username);
    Page<Ingredient> findByUsername(String username, Pageable pageable);

    @Query("SELECT i FROM Ingredient i WHERE i.username = :username OR i.username IS NULL")
    List<Ingredient> findAllForUser(@Param("username") String username);

}

