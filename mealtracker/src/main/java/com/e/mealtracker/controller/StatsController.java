package com.e.mealtracker.controller;

import com.e.mealtracker.dto.DailyStatsDto;
import com.e.mealtracker.dto.RecipePortionRequest;
import com.e.mealtracker.service.StatsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/daily/add")
    public ResponseEntity<DailyStatsDto> addPortion(
            @RequestBody RecipePortionRequest portion,
            @AuthenticationPrincipal UserDetails userDetails) {
        DailyStatsDto stats = statsService.addPortionAndReturnTodayStats(portion, userDetails.getUsername());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/daily")
    public ResponseEntity<DailyStatsDto> getTodayStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        DailyStatsDto stats = statsService.getTodayStats(userDetails.getUsername());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/daily/{date}")
    public ResponseEntity<DailyStatsDto> getStatsByDate(
            @PathVariable String date,
            @AuthenticationPrincipal UserDetails userDetails) {
        LocalDate parsedDate = LocalDate.parse(date);
        DailyStatsDto stats = statsService.getStatsByDate(parsedDate, userDetails.getUsername());
        return ResponseEntity.ok(stats);
    }
}

