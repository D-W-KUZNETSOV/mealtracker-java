package com.e.mealtracker.repository;

import com.e.mealtracker.domain.MealPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealPlanItemRepository extends JpaRepository<MealPlanItem, Long> {

    List<MealPlanItem> findAllByPlanId(Long planId);

    List<MealPlanItem> findAllByPlanIdAndPlanDateBetween(
            Long planId, LocalDate start, LocalDate end);

    List<MealPlanItem> findAllByPlanIdAndPlanDate(Long planId, LocalDate date);
}