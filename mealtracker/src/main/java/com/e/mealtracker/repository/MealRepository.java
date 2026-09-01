package com.e.mealtracker.repository;

import com.e.mealtracker.domain.Meal;
import com.e.mealtracker.domain.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    /**
     * Ищет приём пищи по типу (завтрак/обед и т.д.) и дате.
     * Нужно для DataInitializer, чтобы не создавать дубли на один день.
     */
    Optional<Meal> findByTypeAndDate(MealType type, LocalDate date);
}
