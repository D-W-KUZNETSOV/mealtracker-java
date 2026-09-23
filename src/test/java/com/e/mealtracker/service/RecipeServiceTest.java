package com.e.mealtracker.service;

import com.e.mealtracker.domain.*;
import com.e.mealtracker.dto.CreateRecipeRequest;
import com.e.mealtracker.dto.IngredientWeightDto;
import com.e.mealtracker.dto.RecipeDto;
import com.e.mealtracker.entity.Role;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.repository.IngredientRepository;
import com.e.mealtracker.repository.RecipeIngredientRepository;
import com.e.mealtracker.repository.RecipeRepository;
import com.e.mealtracker.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты RecipeService")
class RecipeServiceTest {

    @Mock private RecipeRepository recipeRepository;
    @Mock private IngredientRepository ingredientRepository;
    @Mock private RecipeIngredientRepository recipeIngredientRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private RecipeService recipeService;

    // ============================================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ============================================================

    private User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword("dummy");
        user.setRole(Role.USER);
        return user;
    }

    private Ingredient createIngredient(Long id, String name,
                                        Double proteins, Double fats, Double carbs) {
        Ingredient ing = new Ingredient();
        ing.setId(id);
        ing.setName(name);
        ing.setUsername("SYSTEM");
        ing.setProteinsPer100g(proteins);
        ing.setFatsPer100g(fats);
        ing.setCarbsPer100g(carbs);
        return ing;
    }

    private CreateRecipeRequest createRequest(String name, String category,
                                              List<IngredientWeightDto> ingredients) {
        CreateRecipeRequest req = new CreateRecipeRequest();
        req.setName(name);
        req.setCategory(category);
        req.setIngredients(ingredients);
        req.setDescription("test description");
        req.setImageUrl("http://example.com/img.png");
        return req;
    }

    private IngredientWeightDto weight(Long id, double grams) {
        IngredientWeightDto dto = new IngredientWeightDto();
        dto.setIngredientId(id);
        dto.setWeightInGrams(grams);
        return dto;
    }

    private void stubSaveEcho() {
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(inv -> {
            Recipe r = inv.getArgument(0);
            if (r.getId() == null) r.setId(100L);
            return r;
        });
    }

    // ============================================================
    // HAPPY PATH
    // ============================================================

    @Test
    @DisplayName("saveRecipe: считает КБЖУ и per-100g, вызывает save() один раз")
    void shouldCalculateNutritionAndSaveOnce() {
        User user = createUser(1L, "dmitriy");
        Ingredient chicken = createIngredient(10L, "Куриная грудка", 31.0, 3.6, 0.0);
        Ingredient buckwheat = createIngredient(11L, "Гречка варёная", 4.2, 1.1, 21.3);

        when(userRepository.findByUsername("dmitriy")).thenReturn(Optional.of(user));
        when(ingredientRepository.findById(10L)).thenReturn(Optional.of(chicken));
        when(ingredientRepository.findById(11L)).thenReturn(Optional.of(buckwheat));
        stubSaveEcho();

        CreateRecipeRequest req = createRequest(
                "Обед", "LUNCH",
                List.of(weight(10L, 200.0), weight(11L, 150.0))
        );

        RecipeDto result = recipeService.saveRecipe(req, "dmitriy");

        // Курица: белки 31*2=62, жиры 3.6*2=7.2, углеводы 0
        // Гречка: белки 4.2*1.5=6.3, жиры 1.1*1.5=1.65, углеводы 21.3*1.5=31.95
        // Итого: белки 68.30, жиры 8.85, углеводы 31.95
        // Калории: белки*4 + жиры*9 + углеводы*4 = 273.2 + 79.65 + 127.8 = 480.65

        assertThat(result.getTotalProteins()).isEqualByComparingTo("68.30");
        assertThat(result.getTotalFats()).isEqualByComparingTo("8.85");
        assertThat(result.getTotalCarbs()).isEqualByComparingTo("31.95");
        assertThat(result.getTotalCalories()).isEqualByComparingTo("480.65");

        // Один save() — каскад сохранит RecipeIngredient
        verify(recipeRepository, times(1)).save(any(Recipe.class));
        verifyNoInteractions(recipeIngredientRepository);
    }

    @Test
    @DisplayName("saveRecipe: проставляет name, description, imageUrl, category, user")
    void shouldFillRecipeFields() {
        User user = createUser(1L, "dmitriy");
        Ingredient ing = createIngredient(10L, "Курица", 31.0, 3.6, 0.0);

        when(userRepository.findByUsername("dmitriy")).thenReturn(Optional.of(user));
        when(ingredientRepository.findById(10L)).thenReturn(Optional.of(ing));
        stubSaveEcho();

        CreateRecipeRequest req = createRequest("Обед", "lunch", List.of(weight(10L, 100.0)));

        recipeService.saveRecipe(req, "dmitriy");

        ArgumentCaptor<Recipe> captor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepository).save(captor.capture());
        Recipe saved = captor.getValue();

        assertThat(saved.getName()).isEqualTo("Обед");
        assertThat(saved.getCategory()).isEqualTo(MealType.LUNCH);   // lowercase → enum
        assertThat(saved.getDescription()).isEqualTo("test description");
        assertThat(saved.getImageUrl()).isEqualTo("http://example.com/img.png");
        assertThat(saved.getUser()).isSameAs(user);
        assertThat(saved.getIngredients()).hasSize(1);
    }

    // ============================================================
    // ОШИБКИ
    // ============================================================

    @Test
    @DisplayName("saveRecipe: юзер не найден → IllegalArgumentException")
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        CreateRecipeRequest req = createRequest("Обед", "LUNCH",
                List.of(weight(10L, 100.0)));

        assertThatThrownBy(() -> recipeService.saveRecipe(req, "ghost"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining("ghost");

        verifyNoInteractions(recipeRepository);
    }

    @Test
    @DisplayName("saveRecipe: ингредиент не найден → IllegalArgumentException")
    void shouldThrowWhenIngredientNotFound() {
        User user = createUser(1L, "dmitriy");
        when(userRepository.findByUsername("dmitriy")).thenReturn(Optional.of(user));
        when(ingredientRepository.findById(99L)).thenReturn(Optional.empty());

        CreateRecipeRequest req = createRequest("Обед", "LUNCH",
                List.of(weight(99L, 100.0)));

        assertThatThrownBy(() -> recipeService.saveRecipe(req, "dmitriy"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");

        verifyNoInteractions(recipeRepository);
    }

    @Test
    @DisplayName("saveRecipe: ингредиент без КБЖУ → IllegalArgumentException со списком имён")
    void shouldThrowWhenNutritionalDataMissing() {
        User user = createUser(1L, "dmitriy");
        Ingredient noProteins = createIngredient(10L, "Курица", null, 3.6, 0.0);
        Ingredient noFats = createIngredient(11L, "Гречка", 4.2, null, 21.3);

        when(userRepository.findByUsername("dmitriy")).thenReturn(Optional.of(user));
        when(ingredientRepository.findById(10L)).thenReturn(Optional.of(noProteins));
        when(ingredientRepository.findById(11L)).thenReturn(Optional.of(noFats));

        CreateRecipeRequest req = createRequest("Обед", "LUNCH",
                List.of(weight(10L, 100.0), weight(11L, 100.0)));

        assertThatThrownBy(() -> recipeService.saveRecipe(req, "dmitriy"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("КБЖУ")
                .hasMessageContaining("Курица")
                .hasMessageContaining("Гречка");

        verify(recipeRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveRecipe: пустой список ингредиентов → totalWeight=0, per-100g=0")
    void shouldHandleEmptyIngredients() {
        User user = createUser(1L, "dmitriy");
        when(userRepository.findByUsername("dmitriy")).thenReturn(Optional.of(user));
        stubSaveEcho();

        CreateRecipeRequest req = createRequest("Вода", "LUNCH", List.of());

        RecipeDto result = recipeService.saveRecipe(req, "dmitriy");

        assertThat(result.getTotalCalories()).isEqualByComparingTo("0.00");
        assertThat(result.getTotalProteins()).isEqualByComparingTo("0.00");
        assertThat(result.getTotalFats()).isEqualByComparingTo("0.00");
        assertThat(result.getTotalCarbs()).isEqualByComparingTo("0.00");

        ArgumentCaptor<Recipe> captor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepository).save(captor.capture());
        Recipe saved = captor.getValue();

        assertThat(saved.getCaloriesPer100g()).isEqualByComparingTo("0");
        assertThat(saved.getProteinPer100g()).isEqualByComparingTo("0");
        assertThat(saved.getFatPer100g()).isEqualByComparingTo("0");
        assertThat(saved.getCarbsPer100g()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("saveRecipe: невалидная категория → category=null, не падает")
    void shouldIgnoreInvalidCategory() {
        User user = createUser(1L, "dmitriy");
        Ingredient ing = createIngredient(10L, "Курица", 31.0, 3.6, 0.0);
        when(userRepository.findByUsername("dmitriy")).thenReturn(Optional.of(user));
        when(ingredientRepository.findById(10L)).thenReturn(Optional.of(ing));
        stubSaveEcho();

        CreateRecipeRequest req = createRequest("Обед", "INVALID_CATEGORY",
                List.of(weight(10L, 100.0)));

        recipeService.saveRecipe(req, "dmitriy");

        ArgumentCaptor<Recipe> captor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepository).save(captor.capture());
        assertThat(captor.getValue().getCategory()).isNull();
    }

    @Test
    @DisplayName("saveRecipe: category=null → category=null, не падает")
    void shouldHandleNullCategory() {
        User user = createUser(1L, "dmitriy");
        Ingredient ing = createIngredient(10L, "Курица", 31.0, 3.6, 0.0);
        when(userRepository.findByUsername("dmitriy")).thenReturn(Optional.of(user));
        when(ingredientRepository.findById(10L)).thenReturn(Optional.of(ing));
        stubSaveEcho();

        CreateRecipeRequest req = createRequest("Обед", null, List.of(weight(10L, 100.0)));

        recipeService.saveRecipe(req, "dmitriy");

        ArgumentCaptor<Recipe> captor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepository).save(captor.capture());
        assertThat(captor.getValue().getCategory()).isNull();
    }

    // ============================================================
    // DELETE / TOGGLE (короткие проверки прав)
    // ============================================================

    @Test
    @DisplayName("deleteRecipeByUser: рецепт не найден → RecipeNotFoundException")
    void shouldThrowWhenDeletingForeignRecipe() {
        User user = createUser(1L, "dmitriy");
        when(userRepository.findByUsername("dmitriy")).thenReturn(Optional.of(user));
        when(recipeRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.deleteRecipeByUser(99L, "dmitriy"))
                .isInstanceOf(com.e.mealtracker.exception.RecipeNotFoundException.class);

        verify(recipeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("toggleRecipeVisibility: PUBLIC → PRIVATE")
    void shouldTogglePublicToPrivate() {
        User user = createUser(1L, "dmitriy");
        Recipe recipe = new Recipe();
        recipe.setId(5L);
        recipe.setName("Обед");
        recipe.setUser(user);
        recipe.setVisibility(RecipeVisibility.PUBLIC);

        when(userRepository.findByUsername("dmitriy")).thenReturn(Optional.of(user));
        when(recipeRepository.findByIdAndUser(5L, user)).thenReturn(Optional.of(recipe));
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = recipeService.toggleRecipeVisibility(5L, "dmitriy");

        assertThat(result.visibility()).isEqualTo(RecipeVisibility.PRIVATE);
        verify(recipeRepository).save(recipe);
    }
}