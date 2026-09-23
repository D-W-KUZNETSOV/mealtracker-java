package com.e.mealtracker.util;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Пол пользователя")
public enum Gender {

    @Schema(description = "Мужской")
    MALE,

    @Schema(description = "Женский")
    FEMALE
}