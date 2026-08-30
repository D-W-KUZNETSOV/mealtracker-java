package com.e.mealtracker.controller;

import com.e.mealtracker.dto.DailyStatsDto;
import com.e.mealtracker.dto.RecipePortionRequest;
import com.e.mealtracker.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/daily")
    public ResponseEntity<DailyStatsDto> calculateDailyStats(@RequestBody List<RecipePortionRequest> portions) {
        DailyStatsDto stats = statsService.calculateStats(portions);
        return ResponseEntity.ok(stats);
    }
}
