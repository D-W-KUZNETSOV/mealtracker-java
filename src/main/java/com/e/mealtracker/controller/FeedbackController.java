package com.e.mealtracker.controller;

import com.e.mealtracker.dto.FeedbackRequest;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.service.EmailService;
import com.e.mealtracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final EmailService emailService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Отправить фидбек (требует JWT)")
    public ResponseEntity<Void> sendFeedback(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody FeedbackRequest request) {

        User user = userService.findByUsername(userDetails.getUsername());

        emailService.sendFeedback(
                user.getUsername(),
                user.getEmail(),
                request.getContactEmail(),
                request.getMessage()
        );

        return ResponseEntity.noContent().build();   // 204
    }
}