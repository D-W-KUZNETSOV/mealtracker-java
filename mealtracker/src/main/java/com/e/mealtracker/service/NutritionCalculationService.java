package com.e.mealtracker.service;

import com.e.mealtracker.domain.UserGoals;
import com.e.mealtracker.dto.TargetProteinResponse;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.entity.UserProfile;
import com.e.mealtracker.repository.UserGoalsRepository;
import com.e.mealtracker.util.ActivityLevel;
import com.e.mealtracker.util.AgeCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NutritionCalculationService {

    private final UserGoalsRepository userGoalsRepository;

    private static final Map<String, Double> ACTIVITY_MULTIPLIERS = Map.of(
            "LOW", 1.2,
            "MODERATE", 1.55,
            "HIGH", 1.725
    );

    @Transactional
    public UserGoals setUserGoals(User user, double currentWeightKg, double proteinPerKg,
                                  Integer targetCalories, ActivityLevel activityLevel) {
        Optional<UserGoals> existingOpt =
                userGoalsRepository.findFirstByUserOrderByCreatedAtDesc(user);
        UserGoals goals;

        if (existingOpt.isPresent()) {
            goals = existingOpt.get();
            goals.setCurrentWeightKg(currentWeightKg);
            goals.setProteinPerKg(proteinPerKg);
            goals.setTargetCalories(targetCalories);
            goals.setActivityLevel(activityLevel);
        } else {
            goals = new UserGoals();
            goals.setUser(user);
            goals.setCurrentWeightKg(currentWeightKg);
            goals.setProteinPerKg(proteinPerKg);
            goals.setTargetCalories(targetCalories);
            goals.setActivityLevel(activityLevel);
        }

        return userGoalsRepository.save(goals);
    }

    public TargetProteinResponse calculateTargetProteinFromGoals(User user) {
        Optional<UserGoals> goalsOpt =
                userGoalsRepository.findFirstByUserOrderByCreatedAtDesc(user);

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

    public Optional<UserGoals> getCurrentGoals(User user) {
        return userGoalsRepository.findFirstByUserOrderByCreatedAtDesc(user);
    }

    public BigDecimal calculateDailyCalories(UserProfile profile) {
        if (profile == null) {
            log.warn("Профиль пользователя отсутствует");
            return BigDecimal.ZERO;
        }
        if (profile.getActivityLevel() == null) {
            throw new IllegalStateException("Activity level is not set for user profile");
        }

        BigDecimal currentWeight = profile.getCurrentWeightKg();
        if (currentWeight == null || currentWeight.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Текущий вес не указан или некорректен: {}", currentWeight);
            return BigDecimal.ZERO;
        }

        if (profile.getDateOfBirth() == null) {
            log.warn("Дата рождения не указана для расчёта калорий");
            return BigDecimal.ZERO;
        }
        if (profile.getHeightCm() == null) {
            log.warn("Рост не указан для расчёта калорий");
            return BigDecimal.ZERO;
        }
        if (profile.getGender() == null) {
            log.warn("Пол не указан для расчёта калорий");
            return BigDecimal.ZERO;
        }

        String gender = profile.getGender();
        if (!"MALE".equals(gender) && !"FEMALE".equals(gender)) {
            log.warn("Некорректное значение пола '{}'. Ожидаются MALE или FEMALE", gender);
            return BigDecimal.ZERO;
        }

        int age = AgeCalculator.calculateAge(profile.getDateOfBirth());
        int heightCm = profile.getHeightCm();
        double weightKg = currentWeight.doubleValue();

        BigDecimal bmr;
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

        double multiplier = ACTIVITY_MULTIPLIERS.getOrDefault(profile.getActivityLevel(), 1.55);
        BigDecimal tdee = bmr.multiply(BigDecimal.valueOf(multiplier));

        if (profile.getTargetWeightKg() != null
                && profile.getTargetWeightKg().compareTo(currentWeight) < 0) {
            tdee = tdee.subtract(BigDecimal.valueOf(500));
        }

        return tdee.setScale(0, RoundingMode.HALF_UP);
    }
}




