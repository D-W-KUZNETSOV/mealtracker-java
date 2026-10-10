package com.e.mealtracker.controller;

import com.e.mealtracker.dto.ImportResultDto;
import com.e.mealtracker.service.RecipeImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class RecipeImportController {

    private final RecipeImportService recipeImportService;

    /**
     * Импортирует рецепты из JSON-файла.
     * Формат: multipart/form-data, поле "file".
     */
    @PostMapping(value = "/import", consumes = "multipart/form-data")
    @Operation(summary = "Импорт рецептов из JSON")
    public ResponseEntity<ImportResultDto> importRecipes(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();

        if (file.isEmpty()) {
            ImportResultDto err = new ImportResultDto();
            err.getErrors().add("Файл пустой");
            return ResponseEntity.badRequest().body(err);
        }

        try (InputStream is = file.getInputStream()) {
            ImportResultDto result = recipeImportService.importFromJson(is, username);
            log.info("Импорт завершён для {}: created={}, skipped={}, failed={}",
                    username, result.getCreated(), result.getSkipped(), result.getFailed());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Ошибка импорта", e);
            ImportResultDto err = new ImportResultDto();
            err.getErrors().add("Ошибка: " + e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }
}
