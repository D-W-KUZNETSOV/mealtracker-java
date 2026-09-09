package com.e.mealtracker.service;

import com.e.mealtracker.domain.UserGoals;
import com.e.mealtracker.dto.TargetProteinResponse;
import com.e.mealtracker.entity.UserProfile;
import com.e.mealtracker.repository.UserGoalsRepository;
import com.e.mealtracker.util.ActivityLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
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

    private static final Map<String, Double> ACTIVITY_MULTIPLIERS = Map.of(
            "LOW", 1.2,
            "MODERATE", 1.55,
            "HIGH", 1.725
    );

    /**
     * currentWeightKg — текущий вес (можно брать из последнего взвешивания или передавать из DTO)
     */
    public BigDecimal calculateDailyCalories(UserProfile profile, BigDecimal currentWeightKg) {
        if (profile == null || currentWeightKg == null || profile.getDateOfBirth() == null) {
            return BigDecimal.ZERO;
        }

        int age = calculateAge(profile.getDateOfBirth());
        int heightCm = profile.getHeightCm() != null ? profile.getHeightCm() : 0;
        String gender = profile.getGender();

        // Mifflin-St Jeor
        BigDecimal bmr;
        double weightKg = currentWeightKg.doubleValue();

        if ("MALE".equals(gender)) {
            bmr = BigDecimal.valueOf(10 * weightKg)
                    .add(BigDecimal.valueOf(6.25 * heightCm))
                    .subtract(BigDecimal.valueOf(5 * age))
                    .add(BigDecimal.valueOf(5));
        } else {
            bmr = BigDecimal.valueOf(10 * weightKg)
                    .add(BigDecimal.valueOf(6.25 * heightCm))
                    .subtract(BigDecimal.valueOf(5 * age))
                    .subtract(BigDecimal.valueOf(161));
        }

        double multiplier = ACTIVITY_MULTIPLIERS.getOrDefault(
                profile.getActivityLevel(), 1.55
        );
        BigDecimal tdee = bmr.multiply(BigDecimal.valueOf(multiplier));

        // Дефицит 500 ккал, если цель — снижение веса
        if (profile.getTargetWeightKg() != null
                && profile.getTargetWeightKg().compareTo(currentWeightKg) < 0) {
            tdee = tdee.subtract(BigDecimal.valueOf(500));
        }

        return tdee.setScale(0, RoundingMode.HALF_UP);
    }

    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}


