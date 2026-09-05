package com.e.mealtracker.service;

import com.e.mealtracker.domain.UserGoals;
import com.e.mealtracker.dto.TargetProteinResponse;
import com.e.mealtracker.repository.UserGoalsRepository;
import com.e.mealtracker.util.ActivityLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NutritionCalculationService {

    private final UserGoalsRepository userGoalsRepository;

    @Transactional
    public UserGoals setUserGoals(String username, double currentWeightKg, double proteinPerKg,
                                  Integer targetCalories, ActivityLevel activityLevel) {
        Optional<UserGoals> existingOpt =
                userGoalsRepository.findFirstByUsernameOrderByCreatedAtDesc(username);
        UserGoals goals;

        if (existingOpt.isPresent()) {
            goals = existingOpt.get();
            goals.setCurrentWeightKg(currentWeightKg);
            goals.setProteinPerKg(proteinPerKg);
            goals.setTargetCalories(targetCalories);
            goals.setActivityLevel(activityLevel);
        } else {
            goals = new UserGoals();
            goals.setUsername(username);
            goals.setCurrentWeightKg(currentWeightKg);
            goals.setProteinPerKg(proteinPerKg);
            goals.setTargetCalories(targetCalories);
            goals.setActivityLevel(activityLevel);
        }

        return userGoalsRepository.save(goals);
    }

    public TargetProteinResponse calculateTargetProteinFromGoals(String username) {
        Optional<UserGoals> goalsOpt =
                userGoalsRepository.findFirstByUsernameOrderByCreatedAtDesc(username);

        double weightKg;
        double proteinPerKg;
        double activityMultiplier;

        if (goalsOpt.isPresent()) {
            UserGoals goals = goalsOpt.get();
            weightKg = goals.getCurrentWeightKg();
            proteinPerKg = goals.getProteinPerKg();
            activityMultiplier = goals.getActivityLevel().getMultiplier();
        } else {
            weightKg = 81.0;
            proteinPerKg = 1.6;
            activityMultiplier = ActivityLevel.SEDENTARY.getMultiplier();
        }

        double targetProtein = weightKg * proteinPerKg * activityMultiplier;
        return new TargetProteinResponse(weightKg, targetProtein);
    }

    public Optional<UserGoals> getCurrentGoals(String username) {
        return userGoalsRepository.findFirstByUsernameOrderByCreatedAtDesc(username);
    }
}

