package com.e.mealtracker.controller;

import com.e.mealtracker.entity.Role;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PostMapping("/purge-deleted")
    @Operation(summary = "Hard delete users, удалённых > 30 дней назад (только ADMIN)")
    public ResponseEntity<Map<String, Integer>> purgeDeleted(
            @AuthenticationPrincipal UserDetails userDetails) {

        User current = userService.findByUsername(userDetails.getUsername());
        if (current.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Только для администраторов");
        }

        int deleted = userService.purgeDeletedUsers(30);
        return ResponseEntity.ok(Map.of("deleted", deleted));
    }
}
