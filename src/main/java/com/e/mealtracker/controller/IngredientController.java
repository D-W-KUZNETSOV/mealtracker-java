package com.e.mealtracker.controller;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.dto.*;
import com.e.mealtracker.repository.IngredientRepository;
import com.e.mealtracker.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@SecurityRequirement(name = "BearerAuth")
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;
    private final IngredientRepository ingredientRepository;

    /**
     * Создаёт новый ингредиент для текущего пользователя.
     * Принимает DTO с данными ингредиента и имя пользователя из контекста безопасности.
     * Возвращает DTO созданного ингредиента.
     */
    @PostMapping
    @Operation(summary = "Создать новый ингредиент")
    public IngredientDto createIngredient(
            @Valid @RequestBody CreateIngredientRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Ingredient ingredient = ingredientService.saveIngredient(request, userDetails.getUsername());
        return IngredientDto.fromEntity(ingredient);
    }

    /**
     * Получает список всех ингредиентов, принадлежащих текущему пользователю.
     * Фильтрация происходит строго по имени пользователя (защита от просмотра чужих данных).
     * Возвращает список DTO ингредиентов.
     */
    @GetMapping
    @Operation(summary = "Получить все ингредиенты текущего пользователя")
    public List<IngredientDto> getAllIngredients(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ingredientService.findAllByUsername(userDetails.getUsername())
                .stream()
                .map(IngredientDto::fromEntity)
                .toList();
    }

    /**
     * Получает все базовые (системные) ингредиенты.
     * Использует специальную константу SYSTEM_USERNAME для фильтрации системных записей.
     * Возвращает список DTO базовых ингредиентов.
     */
    @GetMapping("/base")
    @Operation(summary = "Получить все базовые (системные) ингредиенты")
    public List<IngredientDto> getBaseIngredients() {
        return ingredientRepository
                .findAllByUsername(Ingredient.SYSTEM_USERNAME)
                .stream()
                .map(IngredientDto::fromEntity)
                .toList();
    }

    /**
     * Выполняет поиск базовых ингредиентов по частичному совпадению названия.
     * Если query пустой или null — возвращает все системные ингредиенты.
     * Поиск регистронезависимый.
     * Возвращает список DTO найденных ингредиентов.
     */
    @GetMapping("/base/search")
    @Operation(summary = "Поиск базовых ингредиентов по названию")
    public List<IngredientDto> searchBaseIngredients(@RequestParam(required = false) String query) {
        List<Ingredient> list;
        if (query == null || query.isBlank()) {
            list = ingredientRepository.findAllByUsername(Ingredient.SYSTEM_USERNAME);
        } else {
            list = ingredientRepository.findByNameLowerContainingAndUsername(
                    query.toLowerCase(), Ingredient.SYSTEM_USERNAME);
        }
        return list.stream().map(IngredientDto::fromEntity).toList();
    }

    /**
     * Удаляет ингредиент по ID, если он принадлежит текущему пользователю.
     * Проверяет существование и принадлежность записи перед удалением.
     * При отсутствии записи возвращает 404, при успешном удалении — 204 No Content.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить ингредиент по ID")
    public ResponseEntity<Void> deleteIngredient(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (!ingredientService.existsById(id, userDetails.getUsername())) {
            return ResponseEntity.notFound().build();
        }
        ingredientService.deleteById(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновляет данные ингредиента по ID.
     * Гарантирует, что пользователь может обновлять только свои ингредиенты.
     * Возвращает обновлённый DTO ингредиента в теле ответа (200 OK).
     */
    @PutMapping("/{id}")
    @Operation(summary = "Обновить ингредиент по ID")
    public ResponseEntity<IngredientResponseDto> updateIngredient(
            @PathVariable Long id,
            @RequestBody IngredientUpdateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        IngredientResponseDto updated = ingredientService.updateById(id, dto, userDetails.getUsername());
        return ResponseEntity.ok(updated);
    }

    /**
     * Ищет ингредиент по штрихкоду в своей базе (сначала личные, потом базовые SYSTEM).
     * Если не найден — возвращает found = false, а фронт откроет форму для ручного ввода.
     */
    @GetMapping("/barcode/{code}")
    @Operation(summary = "Найти ингредиент по штрихкоду в своей базе")
    public ProductLookupResponse lookupByBarcode(@PathVariable String code,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        // 1. Ищем у текущего юзера
        Optional<Ingredient> own = ingredientRepository.findByBarcodeAndUsername(code, userDetails.getUsername());
        if (own.isPresent()) {
            return toProductLookup(own.get());
        }

        // 2. Ищем в базовых (SYSTEM) — вдруг кто-то уже добавил
        Optional<Ingredient> base = ingredientRepository.findByBarcodeAndUsername(code, Ingredient.SYSTEM_USERNAME);
        if (base.isPresent()) {
            return toProductLookup(base.get());
        }

        // 3. Не нашли
        return ProductLookupResponse.builder()
                .barcode(code)
                .found(false)
                .build();
    }

    private ProductLookupResponse toProductLookup(Ingredient ingredient) {
        return ProductLookupResponse.builder()
                .barcode(ingredient.getBarcode())
                .name(ingredient.getName())
                .caloriesPer100g(ingredient.calculateCaloriesPer100g())
                .proteinsPer100g(ingredient.getProteinsPer100g())
                .fatsPer100g(ingredient.getFatsPer100g())
                .carbsPer100g(ingredient.getCarbsPer100g())
                .id(ingredient.getId()) // 🆕
                .found(true)
                .build();
    }


}






