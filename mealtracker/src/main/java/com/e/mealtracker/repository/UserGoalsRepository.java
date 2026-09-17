package com.e.mealtracker.repository;

import com.e.mealtracker.domain.UserGoals;
import com.e.mealtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserGoalsRepository extends JpaRepository<UserGoals, Long> {

    Optional<UserGoals> findFirstByUserOrderByCreatedAtDesc(User user);
}


