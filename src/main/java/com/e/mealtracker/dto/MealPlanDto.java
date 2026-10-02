package com.e.mealtracker.dto;

import com.e.mealtracker.domain.MealPlan;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MealPlanDto {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<MealPlanItemDto> items;

    public static MealPlanDto fromEntity(MealPlan plan, List<MealPlanItemDto> items) {
        MealPlanDto dto = new MealPlanDto();
        dto.setId(plan.getId());
        dto.setName(plan.getName());
        dto.setStartDate(plan.getStartDate());
        dto.setEndDate(plan.getEndDate());
        dto.setItems(items);
        return dto;
    }
}
