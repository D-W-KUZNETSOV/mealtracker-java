package com.e.mealtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarResponse {

    /** Месяц в формате YYYY-MM */
    private String month;

    /** Список дат с записями (ISO: YYYY-MM-DD) */
    private List<String> days;

    /** Всего активных дней в месяце */
    private int totalDays;

    /** Текущий streak (дней подряд до сегодня) */
    private int currentStreak;

    /** Лучший streak в месяце */
    private int bestStreak;
}