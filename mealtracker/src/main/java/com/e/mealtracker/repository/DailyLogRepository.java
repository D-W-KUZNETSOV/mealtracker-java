package com.e.mealtracker.repository;

import com.e.mealtracker.domain.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    List<DailyLog> findByDate(LocalDate date);
}

