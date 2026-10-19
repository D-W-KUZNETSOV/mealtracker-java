package com.e.mealtracker.repository;

import com.e.mealtracker.domain.DailyLog;
import com.e.mealtracker.domain.FoodEntry;
import com.e.mealtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodEntryRepository extends JpaRepository<FoodEntry, Long> {
    @Query("SELECT f FROM FoodEntry f WHERE f.id = :id AND f.dailyLog.user = :user")
    Optional<FoodEntry> findByIdAndUser(@Param("id") Long id, @Param("user") User user);

    List<FoodEntry> findByDailyLog(DailyLog dailyLog);

    List<FoodEntry> findByDailyLogId(Long dailyLogId);
}
