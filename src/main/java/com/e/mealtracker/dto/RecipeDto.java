package com.e.mealtracker.dto;

import com.e.mealtracker.domain.Recipe;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
public class RecipeDto {
    private Long id;
    private String name;
    private String category;
    private String description;
    private String imageUrl;
    private List<RecipeIngredientDto> ingredients;
    private BigDecimal totalCalories;
    private BigDecimal totalProteins;
    private BigDecimal totalFats;
    private BigDecimal totalCarbs;
    private Double totalWeight;
    private Integer servings;
    private Double servingSizeGrams;
    private List<String> steps;   // 🆕

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static RecipeDto fromEntity(Recipe r) {
        RecipeDto dto = new RecipeDto();
        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setCategory(r.getCategory() != null ? r.getCategory().getDisplayName() : "Без категории");
        dto.setDescription(r.getDescription());
        dto.setImageUrl(r.getImageUrl());
        dto.setServings(r.getServings());
        dto.setServingSizeGrams(r.getServingSizeGrams());

        dto.setTotalCalories(r.getTotalCalories());
        dto.setTotalProteins(r.getTotalProteins());
        dto.setTotalFats(r.getTotalFats());
        dto.setTotalCarbs(r.getTotalCarbs());
        dto.setTotalWeight(r.getTotalWeight());

        dto.setSteps(parseSteps(r.getSteps()));   // 🆕

        if (r.getIngredients() != null) {
            dto.setIngredients(r.getIngredients().stream()
                    .map(RecipeIngredientDto::fromEntity)
                    .collect(Collectors.toList()));
        } else {
            dto.setIngredients(List.of());
        }

        return dto;
    }

    /** JSON → List<String>. При ошибке — пустой список. */
    public static List<String> parseSteps(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return MAPPER.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Не удалось распарсить steps: {}", json, e);
            return Collections.emptyList();
        }
    }

    /** List<String> → JSON. Пустой список → null. */
    public static String serializeSteps(List<String> steps) {
        if (steps == null || steps.isEmpty()) return null;
        try {
            return MAPPER.writeValueAsString(steps);
        } catch (Exception e) {
            log.warn("Не удалось сериализовать steps: {}", steps, e);
            return null;
        }
    }
}