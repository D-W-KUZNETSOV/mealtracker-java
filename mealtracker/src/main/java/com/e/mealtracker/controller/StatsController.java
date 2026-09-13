package com.e.mealtracker.controller;

import com.e.mealtracker.dto.DailyStatsDto;
import com.e.mealtracker.dto.RecipePortionRequest;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.repository.UserRepository;
import com.e.mealtracker.service.StatsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class StatsController {

    private final StatsService statsService;
    private final UserRepository userRepository;

    @PostMapping("/daily/add")
    public ResponseEntity<DailyStatsDto> addPortion(
            @RequestBody RecipePortionRequest portion,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        DailyStatsDto stats = statsService.addPortionAndReturnTodayStats(portion, user);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/daily")
    public ResponseEntity<DailyStatsDto> getTodayStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        DailyStatsDto stats = statsService.getTodayStats(user);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/daily/{date}")
    public ResponseEntity<DailyStatsDto> getStatsByDate(
            @PathVariable String date,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        LocalDate parsedDate = LocalDate.parse(date);
        DailyStatsDto stats = statsService.getStatsByDate(parsedDate, user);
        return ResponseEntity.ok(stats);
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь не найден: " + userDetails.getUsername()));
    }
}

