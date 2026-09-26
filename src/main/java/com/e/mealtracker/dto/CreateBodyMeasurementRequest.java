package com.e.mealtracker.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateBodyMeasurementRequest {

    @NotNull(message = "Дата обязательна")
    private LocalDate measuredAt;

    private BigDecimal weightKg;
    private BigDecimal chestCm;
    private BigDecimal waistCm;
    private BigDecimal bellyCm;
    private BigDecimal hipsCm;
    private BigDecimal thighCm;
    private BigDecimal armCm;
    private BigDecimal neckCm;
    private String note;
}