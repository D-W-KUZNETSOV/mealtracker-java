package com.e.mealtracker.util;

import java.time.LocalDate;
import java.time.Period;

public class AgeCalculator {
    public static Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return null; // сразу видно: дата не задана
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

}
