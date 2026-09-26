package com.e.mealtracker.dto;

import com.e.mealtracker.domain.UserGoals;
import com.e.mealtracker.util.GoalType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserGoalsDto {

    private Long id;
    private double currentWeightKg;
    private double proteinPerKg;
    private Integer targetCalories;
    private GoalType goalType;
    private Double targetProteinOverride;
    private Integer targetCaloriesOverride;



    private LocalDateTime createdAt;

    public static UserGoalsDto fromEntity(UserGoals goals) {
        UserGoalsDto dto = new UserGoalsDto();
        dto.setId(goals.getId());
        dto.setCurrentWeightKg(goals.getCurrentWeightKg());
        dto.setProteinPerKg(goals.getProteinPerKg());
        dto.setTargetCalories(goals.getTargetCalories());
        dto.setCreatedAt(goals.getCreatedAt());
        dto.setGoalType(goals.getGoalType());
        dto.setTargetProteinOverride(goals.getTargetProteinOverride());
        dto.setTargetCaloriesOverride(goals.getTargetCaloriesOverride());
        return dto;
    }
}