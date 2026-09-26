package com.e.mealtracker.dto;

import com.e.mealtracker.entity.BodyMeasurement;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BodyMeasurementDto {

    private Long id;
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
    private LocalDateTime createdAt;

    public static BodyMeasurementDto fromEntity(BodyMeasurement m) {
        BodyMeasurementDto dto = new BodyMeasurementDto();
        dto.setId(m.getId());
        dto.setMeasuredAt(m.getMeasuredAt());
        dto.setWeightKg(m.getWeightKg());
        dto.setChestCm(m.getChestCm());
        dto.setWaistCm(m.getWaistCm());
        dto.setBellyCm(m.getBellyCm());
        dto.setHipsCm(m.getHipsCm());
        dto.setThighCm(m.getThighCm());
        dto.setArmCm(m.getArmCm());
        dto.setNeckCm(m.getNeckCm());
        dto.setNote(m.getNote());
        dto.setCreatedAt(m.getCreatedAt());
        return dto;
    }
}