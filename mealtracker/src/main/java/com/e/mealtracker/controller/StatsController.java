package com.e.mealtracker.controller;

import com.e.mealtracker.dto.DailyStatsDto;
import com.e.mealtracker.dto.RecipePortionRequest;
import com.e.mealtracker.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/daily/add")
    public ResponseEntity<DailyStatsDto> addPortion(@RequestBody RecipePortionRequest portion) {
        DailyStatsDto stats = statsService.addPortionAndReturnTodayStats(portion);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/daily")
    public ResponseEntity<DailyStatsDto> getTodayStats() {
        DailyStatsDto stats = statsService.getTodayStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/daily/{date}")
    public ResponseEntity<DailyStatsDto> getStatsByDate(@PathVariable String date) {
        LocalDate parsedDate = LocalDate.parse(date); // формат YYYY-MM-DD
        DailyStatsDto stats = statsService.getStatsByDate(parsedDate);
        return ResponseEntity.ok(stats);
    }
}
