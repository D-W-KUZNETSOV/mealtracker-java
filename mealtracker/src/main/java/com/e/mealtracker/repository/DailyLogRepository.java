package com.e.mealtracker.repository;

import com.e.mealtracker.domain.DailyLog;
import com.e.mealtracker.domain.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    List<DailyLog> findByDateAndUsername(LocalDate date, String username);
    Optional<Recipe> findByIdAndUsername(Long id, String username);
    Page<DailyLog> findByUsername(String username, Pageable pageable);
}

