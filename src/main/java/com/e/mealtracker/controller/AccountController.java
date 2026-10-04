package com.e.mealtracker.controller;

import com.e.mealtracker.dto.DeleteAccountRequest;
import com.e.mealtracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;

    @DeleteMapping
    @Operation(summary = "Удалить аккаунт (soft delete). Требует пароль для подтверждения.")
    public ResponseEntity<Void> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DeleteAccountRequest request) {
        userService.deleteAccount(userDetails.getUsername(), request.getPassword());
        return ResponseEntity.noContent().build();
    }
}
