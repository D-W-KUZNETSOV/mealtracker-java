package com.e.mealtracker.repository;

import com.e.mealtracker.domain.MealType;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeVisibility;
import com.e.mealtracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Page<Recipe> findAllByUser(User user, Pageable pageable);

    Page<Recipe> findAllByUserAndCategory(User user, MealType category, Pageable pageable);

    Optional<Recipe> findByIdAndUser(Long id, User user);

    Optional<Recipe> findByNameAndUser(String name, User user);

    /** 🆕 Idempotency F5 — найти копию оригинала у юзера */
    Optional<Recipe> findByUserAndOriginalRecipeId(User user, Long originalRecipeId);

    List<Recipe> findByVisibility(RecipeVisibility visibility);

    default List<Recipe> findPublicRecipes() {
        return findByVisibility(RecipeVisibility.PUBLIC);
    }
    @Query(value = "SELECT * FROM recipes r " +
            "WHERE r.user_id = :userId " +
            "AND (:query IS NULL OR r.name_lower LIKE CONCAT('%', :query, '%')) " +
            "AND (:category IS NULL OR r.category = :category) " +
            "AND (:minCal IS NULL OR r.total_calories >= :minCal) " +
            "AND (:maxCal IS NULL OR r.total_calories <= :maxCal) " +
            "AND (:minProt IS NULL OR r.total_proteins >= :minProt)",
            countQuery = "SELECT COUNT(*) FROM recipes r " +
                    "WHERE r.user_id = :userId " +
                    "AND (:query IS NULL OR r.name_lower LIKE CONCAT('%', :query, '%')) " +
                    "AND (:category IS NULL OR r.category = :category) " +
                    "AND (:minCal IS NULL OR r.total_calories >= :minCal) " +
                    "AND (:maxCal IS NULL OR r.total_calories <= :maxCal) " +
                    "AND (:minProt IS NULL OR r.total_proteins >= :minProt)",
            nativeQuery = true)
    Page<Recipe> searchRecipesNative(
            @Param("userId") Long userId,
            @Param("query") String query,
            @Param("category") String category,
            @Param("minCal") BigDecimal minCal,
            @Param("maxCal") BigDecimal maxCal,
            @Param("minProt") BigDecimal minProt,
            Pageable pageable);
}







