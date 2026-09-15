package com.e.mealtracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageUploadController {

    @Value("${upload.path}")
    private String uploadPathString;

    @Operation(summary = "Загрузить картинку (JPG/PNG)")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadImage(
            @Parameter(description = "Файл (JPG/PNG)", required = true)
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Файл не выбран"));
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null
                || !originalName.toLowerCase().endsWith(".jpg")
                && !originalName.toLowerCase().endsWith(".jpeg")
                && !originalName.toLowerCase().endsWith(".png")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Разрешены только JPG/PNG"));
        }

        Path uploadDir = Paths.get(uploadPathString);
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            log.error("Не удалось создать директорию для загрузок", e);
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка сервера при создании папки"));
        }

        String extension = "";
        if (originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID() + extension;
        Path fullPath = uploadDir.resolve(fileName);

        try {
            file.transferTo(fullPath);
            log.info("Картинка успешно сохранена: {}", fullPath.toAbsolutePath());
            return ResponseEntity.ok(Map.of("imageUrl", "/images/" + fileName));
        } catch (IOException e) {
            log.error("Ошибка при сохранении файла", e);
            return ResponseEntity.status(500).body(Map.of("error", "Не удалось сохранить файл на диск"));
        }
    }
}

