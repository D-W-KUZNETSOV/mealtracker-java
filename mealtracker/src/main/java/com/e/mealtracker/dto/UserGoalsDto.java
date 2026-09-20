package com.e.mealtracker.dto;

import com.e.mealtracker.domain.UserGoals;
import com.e.mealtracker.util.ActivityLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserGoalsDto {

    private Long id;
    private double currentWeightKg;
    private double proteinPerKg;
    private Integer targetCalories;

    @Schema(allowableValues = {"SEDENTARY", "LIGHT", "MODERATE", "HIGH", "VERY_HIGH"})
    private ActivityLevel activityLevel;

    private LocalDateTime createdAt;

    public static UserGoalsDto fromEntity(UserGoals goals) {
        UserGoalsDto dto = new UserGoalsDto();
        dto.setId(goals.getId());
        dto.setCurrentWeightKg(goals.getCurrentWeightKg());
        dto.setProteinPerKg(goals.getProteinPerKg());
        dto.setTargetCalories(goals.getTargetCalories());
        dto.setActivityLevel(goals.getActivityLevel());
        dto.setCreatedAt(goals.getCreatedAt());
        return dto;
    }
}