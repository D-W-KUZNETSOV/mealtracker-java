package com.e.mealtracker.repository;

import com.e.mealtracker.domain.FoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodEntryRepository extends JpaRepository<FoodEntry, Long> {
    List<FoodEntry> findByDailyLogId(Long dailyLogId);
}
