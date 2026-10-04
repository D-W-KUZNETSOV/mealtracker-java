package com.e.mealtracker.repository;

import com.e.mealtracker.entity.User;
import com.e.mealtracker.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(User user);
}