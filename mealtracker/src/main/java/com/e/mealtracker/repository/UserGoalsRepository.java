package com.e.mealtracker.repository;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.domain.UserGoals;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserGoalsRepository extends JpaRepository<UserGoals, Long> {

    Optional<UserGoals> findFirstByUsernameOrderByCreatedAtDesc(String username);
    Page<UserGoals> findByUsername(String username, Pageable pageable);
}

