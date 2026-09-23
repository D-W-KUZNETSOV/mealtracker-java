package com.e.mealtracker.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class BmiCalculator {

    private BmiCalculator() {
    }

    /**
     * Рассчитывает ИМТ (индекс массы тела).
     *
     * @param weightKg вес в килограммах (может быть null)
     * @param heightCm рост в сантиметрах (может быть null, должен быть > 0)
     * @return ИМТ, округлённый до 2 знаков, или null, если данных недостаточно
     */
    public static BigDecimal calculate(BigDecimal weightKg, Integer heightCm) {
        if (weightKg == null || heightCm == null || heightCm <= 0) {
            return null;
        }
        BigDecimal heightM = BigDecimal.valueOf(heightCm)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return weightKg.divide(heightM.pow(2), 2, RoundingMode.HALF_UP);
    }
}