package com.e.mealtracker.util;
import io.swagger.v3.oas.annotations.media.Schema;

public enum ActivityLevel {

    @Schema(description = "Почти нет активности, сидячая работа")
    SEDENTARY(1.2),

    @Schema(description = "1–3 тренировки в неделю, легкая активность")
    LIGHT(1.375),

    @Schema(description = "3–5 тренировок в неделю, умеренная активность")
    MODERATE(1.55),

    @Schema(description = "6–7 дней в неделю, тяжелый физический труд")
    HIGH(1.725),

    @Schema(description = "Экстремальные нагрузки, профессиональный спорт")
    VERY_HIGH(1.9);

    private final double multiplier;

    ActivityLevel(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}

