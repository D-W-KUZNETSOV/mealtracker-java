package com.e.mealtracker.repository;

import com.e.mealtracker.domain.MealType;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class RecipeSpecifications {

    public static Specification<Recipe> byUser(User user) {
        return (root, query, cb) -> cb.equal(root.get("user"), user);
    }

    public static Specification<Recipe> byCategory(MealType category) {
        return (root, query, cb) ->
                category == null ? cb.conjunction() : cb.equal(root.get("category"), category);
    }

    public static Specification<Recipe> nameContains(String query) {
        return (root, q, cb) -> {
            if (query == null || query.isBlank()) return cb.conjunction();
            String pattern = "%" + query.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("name")), pattern);
        };
    }

    public static Specification<Recipe> caloriesBetween(BigDecimal min, BigDecimal max) {
        return (root, q, cb) -> {
            if (min == null && max == null) return cb.conjunction();
            if (min != null && max != null) {
                return cb.between(root.get("totalCalories"), min, max);
            }
            if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("totalCalories"), min);
            }
            return cb.lessThanOrEqualTo(root.get("totalCalories"), max);
        };
    }

    public static Specification<Recipe> proteinAtLeast(BigDecimal min) {
        return (root, q, cb) ->
                min == null ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("totalProteins"), min);
    }
}