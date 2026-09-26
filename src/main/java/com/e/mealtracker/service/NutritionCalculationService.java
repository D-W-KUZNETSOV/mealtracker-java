package com.e.mealtracker.service;

import com.e.mealtracker.domain.UserGoals;
import com.e.mealtracker.dto.TargetProteinResponse;
import com.e.mealtracker.dto.UserGoalsDto;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.entity.UserProfile;
import com.e.mealtracker.repository.UserGoalsRepository;
import com.e.mealtracker.util.ActivityLevel;
import com.e.mealtracker.util.AgeCalculator;
import com.e.mealtracker.util.Gender;
import com.e.mealtracker.util.GoalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NutritionCalculationService {

    private final UserGoalsRepository userGoalsRepository;

    // ❌ УДАЛЕНО: ACTIVITY_MULTIPLIERS — источник истины в ActivityLevel

    @Transactional
    public UserGoalsDto setUserGoals(User user, double currentWeightKg, double proteinPerKg,
                                     Integer targetCalories,
                                     GoalType goalType, Double targetProteinOverride,
                                     Integer targetCaloriesOverride) {
        if (goalType == null) goalType = GoalType.MAINTAIN;

        UserGoals goals = userGoalsRepository
                .findFirstByUserOrderByCreatedAtDesc(user)
                .orElseGet(() -> {
                    UserGoals g = new UserGoals();
                    g.setUser(user);
                    return g;
                });

        goals.setCurrentWeightKg(currentWeightKg);
        goals.setProteinPerKg(proteinPerKg);
        goals.setTargetCalories(targetCalories);
        goals.setGoalType(goalType);
        goals.setTargetProteinOverride(targetProteinOverride);
        goals.setTargetCaloriesOverride(targetCaloriesOverride);

        return UserGoalsDto.fromEntity(userGoalsRepository.save(goals));
    }

    public TargetProteinResponse calculateTargetProteinFromGoals(User user) {
        Optional<UserGoals> goalsOpt = userGoalsRepository.findFirstByUserOrderByCreatedAtDesc(user);

        double weightKg;

        if (goalsOpt.isPresent()) {
            UserGoals goals = goalsOpt.get();
            weightKg = goals.getCurrentWeightKg();
        } else {
            weightKg = 81.0;
        }

        double targetProtein;
        if (goalsOpt.isPresent()) {
            UserGoals goals = goalsOpt.get();

            // Ручная корректировка — приоритет
            if (goals.getTargetProteinOverride() != null) {
                targetProtein = goals.getTargetProteinOverride();
            } else {
                // Авторасчёт по goalType
                double perKg = switch (goals.getGoalType()) {
                    case LOSE_WEIGHT -> 2.0;
                    case MAINTAIN -> 1.6;
                    case GAIN_MUSCLE -> 1.8;
                };
                targetProtein = weightKg * perKg;
            }
        } else {
            // Нет целей — дефолт (поддержание)
            targetProtein = weightKg * 1.6;
        }

        return new TargetProteinResponse(weightKg, targetProtein);
    }

    public Optional<UserGoalsDto> getCurrentGoals(User user) {
        return userGoalsRepository.findFirstByUserOrderByCreatedAtDesc(user)
                .map(UserGoalsDto::fromEntity);
    }

    public BigDecimal calculateDailyCalories(UserProfile profile, UserGoals goals) {
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

        Gender gender = profile.getGender();
        int age = AgeCalculator.calculateAge(profile.getDateOfBirth());
        int heightCm = profile.getHeightCm();
        double weightKg = currentWeight.doubleValue();

        // BMR по формуле Миффлина–Сан Жеора (с полом)
        BigDecimal bmr;
        if (gender == Gender.MALE) {
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

        // TDEE = BMR × коэффициент активности
        double multiplier = profile.getActivityLevel().getMultiplier();
        BigDecimal tdee = bmr.multiply(BigDecimal.valueOf(multiplier));

        // ✅ Сначала override (ручная корректировка)
        if (goals != null && goals.getTargetCaloriesOverride() != null) {
            return BigDecimal.valueOf(goals.getTargetCaloriesOverride());
        }

        // ✅ Потом goalType
        GoalType goalType = (goals != null && goals.getGoalType() != null)
                ? goals.getGoalType()
                : GoalType.MAINTAIN;

        switch (goalType) {
            case LOSE_WEIGHT -> tdee = tdee.multiply(BigDecimal.valueOf(0.8));   // −20%
            case GAIN_MUSCLE -> tdee = tdee.multiply(BigDecimal.valueOf(1.15));  // +15%
            case MAINTAIN -> { /* без изменений */ }
        }

        return tdee.setScale(0, RoundingMode.HALF_UP);
    }
    public BigDecimal calculateDailyCaloriesForUser(User user) {
        UserProfile profile = user.getProfile();
        UserGoals goals = userGoalsRepository
                .findFirstByUserOrderByCreatedAtDesc(user)
                .orElse(null);
        return calculateDailyCalories(profile, goals);
    }
}




