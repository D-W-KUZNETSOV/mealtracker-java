package com.e.mealtracker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageUploadController {

    @Value("${upload.path}")
    private String uploadPathString;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Файл не выбран"));
        }

        // Простая проверка расширения
        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".jpg")
                && !originalName.toLowerCase().endsWith(".jpeg")
                && !originalName.toLowerCase().endsWith(".png")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Разрешены только JPG/PNG"));
        }

        Path uploadDir = Paths.get(uploadPathString);

        try {
            Files.createDirectories(uploadDir); // Создаст папку uploads/images, если её нет
        } catch (IOException e) {
            log.error("Не удалось создать директорию для загрузок", e);
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка сервера при создании папки"));
        }

        // Генерируем уникальное имя, чтобы картинки не перезаписывали друг друга
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;
        Path fullPath = uploadDir.resolve(fileName);

        try {
            file.transferTo(fullPath);
            log.info("Картинка успешно сохранена: {}", fullPath.toAbsolutePath());

            // ВАЖНО: Возвращаем относительный путь.
            // Благодаря настройке spring.web.resources.static-locations в application.properties,
            // Spring сам будет отдавать этот файл по адресу /images/....
            String imageUrl = "/images/" + fileName;

            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (IOException e) {
            log.error("Ошибка при сохранении файла", e);
            return ResponseEntity.status(500).body(Map.of("error", "Не удалось сохранить файл на диск"));
        }
    }
}

