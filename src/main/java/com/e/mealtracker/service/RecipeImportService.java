package com.e.mealtracker.service;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.dto.*;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.repository.IngredientRepository;
import com.e.mealtracker.repository.RecipeRepository;
import com.e.mealtracker.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeImportService {

    private final RecipeService recipeService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    /**
     * Импортирует рецепты из JSON-файла.
     * Формат: { "recipes": [ { "name": "...", "category": "...", ... } ] }
     */
    @Transactional
    public ImportResultDto importFromJson(InputStream inputStream, String username) {
        ImportResultDto result = new ImportResultDto();

        // 1. Парсим JSON
        List<ImportRecipeDto> recipes;
        try {
            JsonNode root = objectMapper.readTree(inputStream);
            JsonNode recipesNode = root.get("recipes");

            if (recipesNode == null || !recipesNode.isArray()) {
                result.getErrors().add("В JSON нет массива 'recipes'");
                return result;
            }

            recipes = new ArrayList<>();
            for (JsonNode recipeNode : recipesNode) {
                ImportRecipeDto dto = objectMapper.treeToValue(recipeNode, ImportRecipeDto.class);
                recipes.add(dto);
            }

            log.info("Импорт: распарсено {} рецептов, username={}", recipes.size(), username);

        } catch (Exception e) {
            log.error("Ошибка парсинга JSON", e);
            result.getErrors().add("Ошибка парсинга JSON: " + e.getMessage());
            return result;
        }

        // 2. Импортируем каждый рецепт
        for (ImportRecipeDto recipeDto : recipes) {
            try {
                boolean created = importRecipe(recipeDto, username);
                if (created) {
                    result.setCreated(result.getCreated() + 1);
                } else {
                    result.setSkipped(result.getSkipped() + 1);
                }
            } catch (Exception e) {
                log.error("Ошибка импорта рецепта '{}'", recipeDto.getName(), e);
                result.setFailed(result.getFailed() + 1);
                result.getErrors().add(recipeDto.getName() + ": " + e.getMessage());
            }
        }

        log.info("Импорт завершён: created={}, skipped={}, failed={}",
                result.getCreated(), result.getSkipped(), result.getFailed());

        return result;
    }
    /**
     * Импортирует один рецепт.
     * Возвращает true — если создан, false — если пропущен (дубль).
     */
    private boolean importRecipe(ImportRecipeDto dto, String username) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Пустое имя рецепта");
        }

        // 1. Проверяем дубль по имени
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        var existing = recipeRepository.findByNameAndUser(dto.getName(), user);
        if (existing.isPresent()) {
            log.info("Рецепт '{}' уже есть — пропускаем", dto.getName());
            return false;
        }

        // 2. Резолвим ингредиенты → собираем CreateRecipeRequest
        CreateRecipeRequest request = new CreateRecipeRequest();
        request.setName(dto.getName());
        request.setCategory(dto.getCategory());

        List<IngredientWeightDto> ingDtos = new ArrayList<>();
        if (dto.getIngredients() != null) {
            for (var ing : dto.getIngredients()) {
                if (ing.getName() == null || ing.getWeightInGrams() == null) continue;

                Ingredient resolved = resolveIngredient(ing.getName(), username);

                IngredientWeightDto ingDto = new IngredientWeightDto();
                ingDto.setIngredientId(resolved.getId());
                ingDto.setWeightInGrams(ing.getWeightInGrams());
                ingDtos.add(ingDto);
            }
        }

        if (ingDtos.isEmpty()) {
            throw new IllegalArgumentException("Нет ингредиентов");
        }

        request.setIngredients(ingDtos);
        request.setSteps(dto.getSteps());

        // 3. Вызываем saveRecipe
        RecipeDto created = recipeService.saveRecipe(request, username);
        log.info("Рецепт создан: id={}, name='{}'", created.getId(), created.getName());
        return true;
    }
    /**
     * Находит ингредиент по имени или создаёт новый.
     * Поиск: сначала базовые, потом личные юзера.
     * Если не найден — создаём личный, КБЖУ=0, категория по словарю.
     */
    private Ingredient resolveIngredient(String name, String username) {
        String nameLower = name.toLowerCase().trim();

        // 1. Ищем среди базовых (SYSTEM)
        var base = ingredientRepository.findFirstByUsernameAndNameLower(
                Ingredient.SYSTEM_USERNAME, nameLower);
        if (base.isPresent()) {
            return base.get();
        }

        // 2. Ищем среди личных юзера
        var personal = ingredientRepository.findByNameLowerAndUsername(nameLower, username);
        if (personal.isPresent()) {
            return personal.get();
        }

        // 3. Не найден — создаём личный
        Ingredient created = new Ingredient();
        created.setName(name.trim());
        created.setUsername(username);
        created.setCaloriesPer100g(0.0);
        created.setProteinsPer100g(0.0);
        created.setFatsPer100g(0.0);
        created.setCarbsPer100g(0.0);
        created.setCategory(guessCategory(name));

        Ingredient saved = ingredientRepository.save(created);
        log.info("Создан ингредиент: '{}' (id={}, category={})",
                name, saved.getId(), saved.getCategory());
        return saved;
    }
    /**
     * Угадывает категорию ингредиента по ключевым словам.
     * Если не нашли — OTHER.
     */
    private String guessCategory(String name) {
        String n = name.toLowerCase();

        Map<String, List<String>> keywords = Map.ofEntries(
                Map.entry("MEAT", List.of("говяд", "свин", "баран", "телят", "курин", "индейк", "бедро", "грудк", "фарш", "печен")),
                Map.entry("FISH", List.of("семг", "лосос", "тунец", "хек", "треск", "сардин", "кревет", "кальмар", "горбуш", "кет")),
                Map.entry("VEGETABLES", List.of("помидор", "огур", "капуст", "морков", "свекл", "кабач", "баклаж", "перец", "лук", "чеснок", "шпинат", "брокколи", "спарж", "салат", "редис", "цукини", "стручков")),
                Map.entry("FRUITS", List.of("яблок", "мандарин", "апельсин", "груш", "авокадо", "лимон", "лайм")),
                Map.entry("GRAINS", List.of("овсян", "греч", "рис", "булгур", "макарон", "хлеб", "хлопья", "мука", "лаваш", "хлебец")),
                Map.entry("DAIRY", List.of("творог", "йогурт", "сыр", "молок", "сметан", "кефир", "ряженк", "скир", "моцарел", "пармезан", "чеддер")),
                Map.entry("EGGS", List.of("яйц", "яиц")),
                Map.entry("NUTS", List.of("миндал", "грецк", "орех", "кешью", "фундук", "семеч", "семян")),
                Map.entry("OILS", List.of("масло оливков", "масло растит", "масло сливочн", "масло гхи", "масло")),
                Map.entry("SWEETS", List.of("мед", "мёд", "сахар", "сухофрукт")),
                Map.entry("DRINKS", List.of("кофе", "чай", "вода", "сок"))
        );

        for (var entry : keywords.entrySet()) {
            for (String kw : entry.getValue()) {
                if (n.contains(kw)) {
                    return entry.getKey();
                }
            }
        }

        return "OTHER";
    }
}