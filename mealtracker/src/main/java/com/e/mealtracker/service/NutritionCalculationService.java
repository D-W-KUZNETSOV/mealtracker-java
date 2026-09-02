package com.e.mealtracker.service;

import com.e.mealtracker.domain.UserGoals;
import com.e.mealtracker.dto.TargetProteinResponse;
import com.e.mealtracker.repository.UserGoalsRepository;
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

        if (goalsOpt.isPresent()) {
            UserGoals goals = goalsOpt.get();
            weightKg = goals.getCurrentWeightKg();
            proteinPerKg = goals.getProteinPerKg();
        } else {
            weightKg = 81.0;
            proteinPerKg = 1.0;
        }

        return new TargetProteinResponse(weightKg, weightKg * proteinPerKg);
    }
    public UserGoals setUserGoals(double currentWeightKg, double proteinPerKg, Integer targetCalories) {
        Optional<UserGoals> existingOpt = userGoalsRepository.findFirstByOrderByCreatedAtDesc();
        UserGoals goals;

        if (existingOpt.isPresent()) {
            // Если запись есть — обновляем её
            goals = existingOpt.get();
            goals.setCurrentWeightKg(currentWeightKg);
            goals.setProteinPerKg(proteinPerKg);
            goals.setTargetCalories(targetCalories);
        } else {
            // Если записи нет — создаём новую
            goals = new UserGoals();
            goals.setCurrentWeightKg(currentWeightKg);
            goals.setProteinPerKg(proteinPerKg);
            goals.setTargetCalories(targetCalories);
            // createdAt заполнится через @PrePersist
        }

        return userGoalsRepository.save(goals);
    }



    // Старый метод с параметром можно оставить, если он где‑то ещё нужен,
    // или пометить как @Deprecated, чтобы не использовать.
    @Deprecated
    public double calculateTargetProteinGrams(double weightKg) {
        // Тут была старая логика — можно удалить или оставить для тестов
        return weightKg * 1.0;
    }
    public Optional<UserGoals> getCurrentGoals() {
        return userGoalsRepository.findFirstByOrderByCreatedAtDesc();
    }

}


