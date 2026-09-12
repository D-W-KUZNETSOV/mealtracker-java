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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты DataInitializer")
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

    private Ingredient createIngredient(String name) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName(name);
        return ingredient;
    }

    private void mockSharedIngredientsExist() {
        when(ingredientRepository.findByNameIgnoreCaseAndUsernameIsNull(eq("Куриная грудка")))
                .thenReturn(Optional.of(createIngredient("Куриная грудка")));
        when(ingredientRepository.findByNameIgnoreCaseAndUsernameIsNull(eq("Гречка варёная")))
                .thenReturn(Optional.of(createIngredient("Гречка варёная")));
    }

    private void mockRecipeExists() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Обед: курица + гречка");
        recipe.setUsername(TEST_USER);
        when(recipeRepository.findByNameAndUsername(anyString(), eq(TEST_USER)))
                .thenReturn(Optional.of(recipe));
    }

    private void mockRecipeIngredientExists() {
        lenient().when(recipeIngredientRepository.countByRecipeAndIngredient(any(Recipe.class), any(Ingredient.class)))
                .thenReturn(1L);
        lenient().when(recipeIngredientRepository.countByRecipe(any(Recipe.class)))
                .thenReturn(2L);
    }

    // ============================================================
    // ТЕСТЫ
    // ============================================================

    @Test
    @DisplayName("Повторный запуск не должен создавать дубликаты рецепта")
    void shouldNotDuplicateRecipeOnMultipleRuns() {
        when(userContextService.getCurrentUsername()).thenReturn(TEST_USER);
        mockSharedIngredientsExist();
        mockRecipeExists();
        mockRecipeIngredientExists();

        dataInitializer.run();

        verify(recipeRepository, never()).save(any(Recipe.class));
        verify(recipeIngredientRepository, never()).save(any(RecipeIngredient.class));
    }

    @Test
    @DisplayName("3 запуска подряд не создают дубликатов")
    void shouldBeIdempotentAcrossMultipleRuns() {
        when(userContextService.getCurrentUsername()).thenReturn(TEST_USER);
        mockSharedIngredientsExist();
        mockRecipeExists();
        mockRecipeIngredientExists();

        for (int i = 0; i < 3; i++) {
            dataInitializer.run();
        }

        verify(recipeRepository, never()).save(any(Recipe.class));
        verify(recipeIngredientRepository, never()).save(any(RecipeIngredient.class));
    }

    @Test
    @DisplayName("При первом запуске создаётся рецепт с ингредиентами")
    void shouldCreateRecipeOnFirstRun() {
        when(userContextService.getCurrentUsername()).thenReturn(TEST_USER);
        mockSharedIngredientsExist();

        when(recipeRepository.findByNameAndUsername(anyString(), eq(TEST_USER)))
                .thenReturn(Optional.empty());
        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(invocation -> {
                    Recipe recipe = invocation.getArgument(0);
                    recipe.setId(1L);
                    return recipe;
                });

        lenient().when(recipeIngredientRepository.countByRecipeAndIngredient(any(Recipe.class), any(Ingredient.class)))
                .thenReturn(0L);
        lenient().when(recipeIngredientRepository.save(any(RecipeIngredient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        dataInitializer.run();

        verify(recipeRepository, times(1)).save(any(Recipe.class));
        verify(recipeIngredientRepository, times(2)).save(any(RecipeIngredient.class));
    }

    @Test
    @DisplayName("При отключенной инициализации ничего не создаётся")
    void shouldNotInitializeWhenDisabled() {
        when(environment.getProperty("app.init-demo-data", "false"))
                .thenReturn("false");

        dataInitializer.run();

        verify(ingredientRepository, never()).findByNameIgnoreCaseAndUsernameIsNull(anyString());
        verify(ingredientRepository, never()).save(any(Ingredient.class));
        verify(recipeRepository, never()).findByNameAndUsername(anyString(), anyString());
    }

    @Test
    @DisplayName("При отсутствии авторизации используется fallback пользователь 'dmitriy'")
    void shouldUseFallbackUserWhenNoAuthentication() {
        when(userContextService.getCurrentUsername()).thenReturn(null);
        mockSharedIngredientsExist();

        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Обед: курица + гречка");
        recipe.setUsername("dmitriy");
        when(recipeRepository.findByNameAndUsername(anyString(), eq("dmitriy")))
                .thenReturn(Optional.of(recipe));

        mockRecipeIngredientExists();

        dataInitializer.run();

        verify(recipeRepository).findByNameAndUsername(anyString(), eq("dmitriy"));
        verify(ingredientRepository, times(1))
                .findByNameIgnoreCaseAndUsernameIsNull(eq("Куриная грудка"));
    }
}

