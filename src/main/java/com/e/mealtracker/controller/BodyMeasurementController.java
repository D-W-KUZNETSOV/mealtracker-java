package com.e.mealtracker.controller;

import com.e.mealtracker.dto.BodyMeasurementDto;
import com.e.mealtracker.dto.CreateBodyMeasurementRequest;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.repository.UserRepository;
import com.e.mealtracker.service.BodyMeasurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/measurements")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class BodyMeasurementController {

    private final BodyMeasurementService service;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Получить все замеры текущего пользователя")
    public ResponseEntity<List<BodyMeasurementDto>> list(Authentication auth) {
        User user = getUser(auth);
        return ResponseEntity.ok(service.listByUser(user));
    }

    @PostMapping
    @Operation(summary = "Добавить новый замер")
    public ResponseEntity<BodyMeasurementDto> create(
            @Valid @RequestBody CreateBodyMeasurementRequest request,
            Authentication auth) {
        User user = getUser(auth);
        BodyMeasurementDto dto = service.create(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить замер по ID")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        User user = getUser(auth);
        service.delete(user, id);
        return ResponseEntity.noContent().build();
    }

    private User getUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь не найден: " + auth.getName()));
    }
}