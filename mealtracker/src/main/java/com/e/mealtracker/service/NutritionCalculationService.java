package com.e.mealtracker.service;

import com.e.mealtracker.domain.UserGoals;
import com.e.mealtracker.dto.TargetProteinResponse;
import com.e.mealtracker.repository.UserGoalsRepository;
import com.e.mealtracker.util.ActivityLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NutritionCalculationService {

    private final UserGoalsRepository userGoalsRepository;

    // Считает по данным из БД. Если нет целей — берёт дефолт.
    public TargetProteinResponse calculateTargetProteinFromGoals() {
        Optional<UserGoals> goalsOpt = userGoalsRepository.findFirstByOrderByCreatedAtDesc();

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
            proteinPerKg = 1.6; // дефолтная норма
            activityMultiplier = ActivityLevel.SEDENTARY.getMultiplier();
        }

        // Итоговая норма: белок на кг × множитель активности
        double targetProtein = weightKg * proteinPerKg * activityMultiplier;

        return new TargetProteinResponse(weightKg, targetProtein);
    }

    // ОСНОВНОЙ МЕТОД - с ActivityLevel
    public UserGoals setUserGoals(double currentWeightKg, double proteinPerKg,
                                  Integer targetCalories, ActivityLevel activityLevel) {
        Optional<UserGoals> existingOpt = userGoalsRepository.findFirstByOrderByCreatedAtDesc();
        UserGoals goals;

        if (existingOpt.isPresent()) {
            // Если запись есть — обновляем её
            goals = existingOpt.get();
            goals.setCurrentWeightKg(currentWeightKg);
            goals.setProteinPerKg(proteinPerKg);
            goals.setTargetCalories(targetCalories);
            goals.setActivityLevel(activityLevel); // ОБНОВЛЯЕМ activityLevel
        } else {
            // Если записи нет — создаём новую
            goals = new UserGoals();
            goals.setCurrentWeightKg(currentWeightKg);
            goals.setProteinPerKg(proteinPerKg);
            goals.setTargetCalories(targetCalories);
            goals.setActivityLevel(activityLevel); // УСТАНАВЛИВАЕМ activityLevel
            // createdAt заполнится через @PrePersist
        }

        return userGoalsRepository.save(goals);
    }

    // Перегруженный метод для обратной совместимости (если нужен)
    // Можно удалить, если не используется
    @Deprecated
    public UserGoals setUserGoals(double currentWeightKg, double proteinPerKg, Integer targetCalories) {
        // Используем дефолтный уровень активности
        return setUserGoals(currentWeightKg, proteinPerKg, targetCalories, ActivityLevel.SEDENTARY);
    }

    @Deprecated
    public double calculateTargetProteinGrams(double weightKg) {
        return weightKg * 1.0;
    }

    public Optional<UserGoals> getCurrentGoals() {
        return userGoalsRepository.findFirstByOrderByCreatedAtDesc();
    }
}

