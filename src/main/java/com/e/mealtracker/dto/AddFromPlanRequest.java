package com.e.mealtracker.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class AddFromPlanRequest {
    private Long planId;
    private LocalDate date;
    private List<Long> itemIds;
}
