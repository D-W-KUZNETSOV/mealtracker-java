package com.e.mealtracker.config;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeIngredient;
import com.e.mealtracker.repository.IngredientRepository;
import com.e.mealtracker.repository.RecipeIngredientRepository;
import com.e.mealtracker.repository.RecipeRepository;
import com.e.mealtracker.service.UserContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты идемпотентности DataInitializer")
class DataInitializerTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    @Mock
    private Environment environment;

    @Mock
    private UserContextService userContextService;

    @InjectMocks
    private DataInitializer dataInitializer;

    private static final String TEST_USER = "testuser";

    @BeforeEach
    void setUp() {
        lenient().when(environment.getProperty("app.init-demo-data", "false"))
                .thenReturn("true");
    }

    // ============================================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ============================================================

    private Ingredient createIngredientWithCorrectData(String name) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName(name);
        ingredient.setUsername(TEST_USER);

        switch (name) {
            case "Куриная грудка":
                ingredient.setFatsPer100g(1.5);
                ingredient.setProteinsPer100g(31.0);
                ingredient.setCarbsPer100g(0.0);
                break;
            case "Гречка варёная":
                ingredient.setFatsPer100g(1.5);
                ingredient.setProteinsPer100g(4.2);
                ingredient.setCarbsPer100g(28.7);
                break;
            case "Яйцо куриное":
                ingredient.setFatsPer100g(11.5);
                ingredient.setProteinsPer100g(12.7);
                ingredient.setCarbsPer100g(0.7);
                break;
            case "Творог 5%":
                ingredient.setFatsPer100g(5.0);
                ingredient.setProteinsPer100g(17.0);
                ingredient.setCarbsPer100g(3.0);
                break;
            case "Огурец свежий":
                ingredient.setFatsPer100g(0.1);
                ingredient.setProteinsPer100g(0.8);
                ingredient.setCarbsPer100g(2.8);
                break;
            case "Рис варёный":
                ingredient.setFatsPer100g(0.3);
                ingredient.setProteinsPer100g(2.7);
                ingredient.setCarbsPer100g(28.0);
                break;
            case "Овсянка на воде":
                ingredient.setFatsPer100g(1.7);
                ingredient.setProteinsPer100g(3.0);
                ingredient.setCarbsPer100g(15.0);
                break;
            case "Молоко 3.2%":
                ingredient.setFatsPer100g(3.6);
                ingredient.setProteinsPer100g(3.2);
                ingredient.setCarbsPer100g(4.8);
                break;
            default:
                ingredient.setFatsPer100g(0.0);
                ingredient.setProteinsPer100g(0.0);
                ingredient.setCarbsPer100g(0.0);
        }
        ingredient.setCaloriesPer100g(ingredient.calculateCaloriesPer100g());
        return ingredient;
    }

    private void mockAllIngredientsExist() {
        String[] names = {
                "Куриная грудка", "Гречка варёная", "Яйцо куриное",
                "Творог 5%", "Огурец свежий", "Рис варёный",
                "Овсянка на воде", "Молоко 3.2%"
        };

        for (String name : names) {
            Ingredient ing = createIngredientWithCorrectData(name);
            when(ingredientRepository.findByNameIgnoreCaseAndUsername(eq(name), eq(TEST_USER)))
                    .thenReturn(Optional.of(ing));
        }
    }

    private void mockRecipeExists() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Обед: курица + гречка");
        recipe.setUsername(TEST_USER);
        when(recipeRepository.findByNameAndUsername(anyString(), eq(TEST_USER)))
                .thenReturn(Optional.of(recipe));
    }

    private void mockRecipeIngredientCount() {
        lenient().when(recipeIngredientRepository.countByRecipeAndIngredient(any(Recipe.class), any(Ingredient.class)))
                .thenReturn(1L);
        lenient().when(recipeIngredientRepository.countByRecipe(any(Recipe.class)))
                .thenReturn(2L);
    }

    // ============================================================
    // 1. ТЕСТЫ
    // ============================================================

    @Test
    @DisplayName("Повторный запуск не должен создавать дубликаты ингредиентов")
    void shouldNotDuplicateIngredientsOnMultipleRuns() {
        when(userContextService.getCurrentUsername()).thenReturn(TEST_USER);
        mockAllIngredientsExist();
        mockRecipeExists();
        mockRecipeIngredientCount();

        dataInitializer.run();

        verify(ingredientRepository, never()).save(any(Ingredient.class));
        verify(ingredientRepository, atLeastOnce())
                .findByNameIgnoreCaseAndUsername(anyString(), eq(TEST_USER));
    }

    @Test
    @DisplayName("Ингредиент обновляется только при изменении данных")
    void shouldUpdateIngredientOnlyWhenDataChanged() {
        when(userContextService.getCurrentUsername()).thenReturn(TEST_USER);

        Ingredient existingIngredient = new Ingredient();
        existingIngredient.setId(1L);
        existingIngredient.setName("Куриная грудка");
        existingIngredient.setUsername(TEST_USER);
        existingIngredient.setFatsPer100g(2.0);
        existingIngredient.setProteinsPer100g(30.0);
        existingIngredient.setCarbsPer100g(1.0);
        existingIngredient.setCaloriesPer100g(existingIngredient.calculateCaloriesPer100g());

        when(ingredientRepository.findByNameIgnoreCaseAndUsername(eq("Куриная грудка"), eq(TEST_USER)))
                .thenReturn(Optional.of(existingIngredient));

        String[] otherNames = {"Гречка варёная", "Яйцо куриное", "Творог 5%",
                "Огурец свежий", "Рис варёный", "Овсянка на воде", "Молоко 3.2%"};
        for (String name : otherNames) {
            Ingredient ing = createIngredientWithCorrectData(name);
            when(ingredientRepository.findByNameIgnoreCaseAndUsername(eq(name), eq(TEST_USER)))
                    .thenReturn(Optional.of(ing));
        }

        mockRecipeExists();
        mockRecipeIngredientCount();

        dataInitializer.run();

        verify(ingredientRepository, times(1)).save(argThat(ing ->
                ing.getName().equals("Куриная грудка") &&
                        ing.getFatsPer100g() == 1.5 &&
                        ing.getProteinsPer100g() == 31.0 &&
                        ing.getCarbsPer100g() == 0.0
        ));
    }

    @Test
    @DisplayName("При первом запуске создаются все 8 ингредиентов")
    void shouldCreateAllIngredientsOnFirstRun() {
        when(userContextService.getCurrentUsername()).thenReturn(TEST_USER);

        // Все ингредиенты отсутствуют при первом поиске
        when(ingredientRepository.findByNameIgnoreCaseAndUsername(anyString(), eq(TEST_USER)))
                .thenReturn(Optional.empty());

        // "Куриная грудка" и "Гречка варёная" ищутся дважды:
        // 1-й вызов (создание ингредиента) → empty → save
        // 2-й вызов (создание рецепта) → найден
        Ingredient chicken = createIngredientWithCorrectData("Куриная грудка");
        chicken.setId(1L);
        when(ingredientRepository.findByNameIgnoreCaseAndUsername(eq("Куриная грудка"), eq(TEST_USER)))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(chicken));

        Ingredient buckwheat = createIngredientWithCorrectData("Гречка варёная");
        buckwheat.setId(1L);
        when(ingredientRepository.findByNameIgnoreCaseAndUsername(eq("Гречка варёная"), eq(TEST_USER)))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(buckwheat));

        // Сохранение ингредиента
        when(ingredientRepository.save(any(Ingredient.class)))
                .thenAnswer(invocation -> {
                    Ingredient saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        // Рецепт не существует → будет создан
        when(recipeRepository.findByNameAndUsername(anyString(), eq(TEST_USER)))
                .thenReturn(Optional.empty());
        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(invocation -> {
                    Recipe recipe = invocation.getArgument(0);
                    recipe.setId(1L);
                    return recipe;
                });

        // Связи рецепта с ингредиентами (lenient — могут не вызываться)
        lenient().when(recipeIngredientRepository.countByRecipeAndIngredient(any(Recipe.class), any(Ingredient.class)))
                .thenReturn(0L);
        lenient().when(recipeIngredientRepository.save(any(RecipeIngredient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        dataInitializer.run();

        verify(ingredientRepository, times(8)).save(any(Ingredient.class));
    }

    @Test
    @DisplayName("3 запуска подряд не создают дубликатов")
    void shouldBeIdempotentAcrossMultipleRuns() {
        when(userContextService.getCurrentUsername()).thenReturn(TEST_USER);
        mockAllIngredientsExist();
        mockRecipeExists();
        mockRecipeIngredientCount();

        for (int i = 0; i < 3; i++) {
            dataInitializer.run();
        }

        verify(ingredientRepository, never()).save(any(Ingredient.class));
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    @DisplayName("При отключенной инициализации данные не создаются")
    void shouldNotInitializeWhenDisabled() {
        when(environment.getProperty("app.init-demo-data", "false"))
                .thenReturn("false");

        dataInitializer.run();

        verify(ingredientRepository, never()).findByNameIgnoreCaseAndUsername(anyString(), anyString());
        verify(ingredientRepository, never()).save(any(Ingredient.class));
        verify(recipeRepository, never()).findByNameAndUsername(anyString(), anyString());
    }

    @Test
    @DisplayName("При отсутствии авторизации используется fallback пользователь 'dmitriy'")
    void shouldUseFallbackUserWhenNoAuthentication() {
        when(userContextService.getCurrentUsername()).thenReturn(null);

        String[] names = {
                "Куриная грудка", "Гречка варёная", "Яйцо куриное",
                "Творог 5%", "Огурец свежий", "Рис варёный",
                "Овсянка на воде", "Молоко 3.2%"
        };
        for (String name : names) {
            Ingredient ing = new Ingredient();
            ing.setId(1L);
            ing.setName(name);
            ing.setUsername("dmitriy");
            ing.setFatsPer100g(1.0);
            ing.setProteinsPer100g(1.0);
            ing.setCarbsPer100g(1.0);
            ing.setCaloriesPer100g(ing.calculateCaloriesPer100g());
            when(ingredientRepository.findByNameIgnoreCaseAndUsername(eq(name), eq("dmitriy")))
                    .thenReturn(Optional.of(ing));
        }

        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Обед: курица + гречка");
        recipe.setUsername("dmitriy");
        when(recipeRepository.findByNameAndUsername(anyString(), eq("dmitriy")))
                .thenReturn(Optional.of(recipe));

        dataInitializer.run();

        verify(ingredientRepository, atLeastOnce())
                .findByNameIgnoreCaseAndUsername(anyString(), eq("dmitriy"));
    }
}
