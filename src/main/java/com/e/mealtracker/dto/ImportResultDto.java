package com.e.mealtracker.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportResultDto {
    private int created;       // сколько рецептов создано
    private int skipped;       // сколько пропущено (дубли)
    private int failed;        // сколько упало с ошибкой
    private List<String> errors = new ArrayList<>();  // тексты ошибок
}