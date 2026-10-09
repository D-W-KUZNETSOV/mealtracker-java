package com.e.mealtracker.repository;

import com.e.mealtracker.domain.DailyLog;
import com.e.mealtracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {

    // Вместо findByDateAndUsername:
   // List<DailyLog> findByUserAndLogDate(User user, LocalDate logDate);

    // findByIdAndUsername удаляем — он некорректен (DailyLog не содержит username)
    // Если нужно найти DailyLog по ID — достаточно findById из JpaRepository

    // Вместо findByUsername:
    Page<DailyLog> findByUser(User user, Pageable pageable);

    // Эти два можно оставить, они правильные:
    Optional<DailyLog> findByUserIdAndLogDate(Long userId, LocalDate logDate);
    Optional<DailyLog> findByUserAndLogDate(User user, LocalDate logDate);
    @Query("""
    SELECT DISTINCT dl.logDate
    FROM DailyLog dl
    WHERE dl.user = :user
      AND dl.logDate >= :from
      AND dl.logDate <= :to
      AND EXISTS (SELECT 1 FROM FoodEntry fe WHERE fe.dailyLog = dl)
    ORDER BY dl.logDate
""")
    List<LocalDate> findActiveDatesByUserAndRange(
            @Param("user") User user,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}


