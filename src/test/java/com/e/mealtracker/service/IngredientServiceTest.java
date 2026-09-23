package com.e.mealtracker.service;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.dto.CreateIngredientRequest;
import com.e.mealtracker.dto.IngredientResponseDto;
import com.e.mealtracker.dto.IngredientUpdateDTO;
import com.e.mealtracker.exception.ResourceNotFoundException;
import com.e.mealtracker.repository.IngredientRepository;
import com.e.mealtracker.repository.RecipeIngredientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты IngredientService")
class IngredientServiceTest {

    @Mock private IngredientRepository ingredientRepository;
    @Mock private RecipeIngredientRepository recipeIngredientRepository;

    @InjectMocks
    private IngredientService ingredientService;

    // ============================================================
    // ВСПОМОГАТЕЛЬНЫЕ
    // ============================================================

    private Ingredient ingredient(Long id, String name, String username,
                                  Double proteins, Double fats, Double carbs) {
        Ingredient ing = new Ingredient();
        ing.setId(id);
        ing.setName(name);
        ing.setUsername(username);
        ing.setProteinsPer100g(proteins);
        ing.setFatsPer100g(fats);
        ing.setCarbsPer100g(carbs);
        return ing;
    }

    private CreateIngredientRequest createRequest(String name,
                                                  Double proteins, Double fats, Double carbs) {
        CreateIngredientRequest req = new CreateIngredientRequest();
        req.setName(name);
        req.setProteinsPer100g(proteins);
        req.setFatsPer100g(fats);
        req.setCarbsPer100g(carbs);
        return req;
    }

    // ============================================================
    // existsById
    // ============================================================

    @Test
    @DisplayName("existsById: возвращает true, если репозиторий нашёл")
    void shouldReturnTrueWhenExists() {
        when(ingredientRepository.existsByIdAndUsername(5L, "dmitriy")).thenReturn(true);

        assertThat(ingredientService.existsById(5L, "dmitriy")).isTrue();
        verify(ingredientRepository).existsByIdAndUsername(5L, "dmitriy");
    }

    @Test
    @DisplayName("existsById: возвращает false, если не найден")
    void shouldReturnFalseWhenNotExists() {
        when(ingredientRepository.existsByIdAndUsername(99L, "dmitriy")).thenReturn(false);

        assertThat(ingredientService.existsById(99L, "dmitriy")).isFalse();
    }

    // ============================================================
    // saveIngredient — новый
    // ============================================================

    @Test
    @DisplayName("saveIngredient: нового ингредиента сохраняет с переданными полями")
    void shouldCreateNewIngredient() {
        when(ingredientRepository.findByNameIgnoreCaseAndUsername("Курица", "dmitriy"))
                .thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class)))
                .thenAnswer(inv -> {
                    Ingredient i = inv.getArgument(0);
                    if (i.getId() == null) i.setId(42L);
                    return i;
                });

        CreateIngredientRequest req = createRequest("Курица", 31.0, 3.6, 0.0);
        Ingredient result = ingredientService.saveIngredient(req, "dmitriy");

        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getName()).isEqualTo("Курица");
        assertThat(result.getUsername()).isEqualTo("dmitriy");
        assertThat(result.getProteinsPer100g()).isEqualTo(31.0);
        assertThat(result.getFatsPer100g()).isEqualTo(3.6);
        assertThat(result.getCarbsPer100g()).isEqualTo(0.0);

        verify(ingredientRepository).save(any(Ingredient.class));
    }

    // ============================================================
    // saveIngredient — существующий
    // ============================================================

    @Test
    @DisplayName("saveIngredient: существующий — обновляет КБЖУ, name не трогает")
    void shouldUpdateExistingIngredientNutrition() {
        Ingredient existing = ingredient(7L, "Курица (старое имя)", "dmitriy",
                20.0, 2.0, 1.0);
        when(ingredientRepository.findByNameIgnoreCaseAndUsername("Курица", "dmitriy"))
                .thenReturn(Optional.of(existing));
        when(ingredientRepository.save(any(Ingredient.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CreateIngredientRequest req = createRequest("Курица", 31.0, 3.6, 0.0);
        Ingredient result = ingredientService.saveIngredient(req, "dmitriy");

        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getName()).isEqualTo("Курица (старое имя)"); // name НЕ перезаписан
        assertThat(result.getProteinsPer100g()).isEqualTo(31.0);
        assertThat(result.getFatsPer100g()).isEqualTo(3.6);
        assertThat(result.getCarbsPer100g()).isEqualTo(0.0);

        verify(ingredientRepository, never()).deleteById(any());
        verify(ingredientRepository).save(existing);
    }

    @Test
    @DisplayName("saveIngredient: поиск идёт по name ignore case + username")
    void shouldSearchIgnoringCase() {
        when(ingredientRepository.findByNameIgnoreCaseAndUsername("курица", "dmitriy"))
                .thenReturn(Optional.of(ingredient(7L, "Курица", "dmitriy", 20.0, 2.0, 1.0)));
        when(ingredientRepository.save(any(Ingredient.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ingredientService.saveIngredient(createRequest("курица", 1.0, 1.0, 1.0), "dmitriy");

        verify(ingredientRepository).findByNameIgnoreCaseAndUsername("курица", "dmitriy");
    }

    // ============================================================
    // findAllByUsername
    // ============================================================

    @Test
    @DisplayName("findAllByUsername: пробрасывает вызов в findAllForUser")
    void shouldReturnAllForUser() {
        List<Ingredient> list = List.of(
                ingredient(1L, "A", "dmitriy", 1.0, 1.0, 1.0),
                ingredient(2L, "B", "dmitriy", 2.0, 2.0, 2.0)
        );
        when(ingredientRepository.findAllForUser("dmitriy")).thenReturn(list);

        List<Ingredient> result = ingredientService.findAllByUsername("dmitriy");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Ingredient::getName).containsExactly("A", "B");
        verify(ingredientRepository).findAllForUser("dmitriy");
    }

    // ============================================================
    // updateById — happy paths
    // ============================================================

    @Test
    @DisplayName("updateById: обновляет только не-null поля")
    void shouldUpdateOnlyProvidedFields() {
        Ingredient existing = ingredient(5L, "Курица", "dmitriy", 20.0, 2.0, 1.0);
        when(ingredientRepository.findByIdAndUsername(5L, "dmitriy"))
                .thenReturn(Optional.of(existing));
        when(ingredientRepository.save(any(Ingredient.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        IngredientUpdateDTO dto = new IngredientUpdateDTO();
        dto.setProteinsPer100g(31.0);   // только это поле
        // fats, carbs, name — null

        IngredientResponseDto result = ingredientService.updateById(5L, dto, "dmitriy");

        assertThat(result.getName()).isEqualTo("Курица");       // не менялось
        assertThat(result.getProteinsPer100g()).isEqualTo(31.0); // обновилось
        assertThat(result.getFatsPer100g()).isEqualTo(2.0);      // не менялось
        assertThat(result.getCarbsPer100g()).isEqualTo(1.0);     // не менялось
    }

    @Test
    @DisplayName("updateById: обновляет все поля, если переданы")
    void shouldUpdateAllProvidedFields() {
        Ingredient existing = ingredient(5L, "Курица", "dmitriy", 20.0, 2.0, 1.0);
        when(ingredientRepository.findByIdAndUsername(5L, "dmitriy"))
                .thenReturn(Optional.of(existing));
        when(ingredientRepository.save(any(Ingredient.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        IngredientUpdateDTO dto = new IngredientUpdateDTO();
        dto.setName("Курица варёная");
        dto.setProteinsPer100g(25.0);
        dto.setFatsPer100g(4.0);
        dto.setCarbsPer100g(0.5);

        IngredientResponseDto result = ingredientService.updateById(5L, dto, "dmitriy");

        assertThat(result.getName()).isEqualTo("Курица варёная");
        assertThat(result.getProteinsPer100g()).isEqualTo(25.0);
        assertThat(result.getFatsPer100g()).isEqualTo(4.0);
        assertThat(result.getCarbsPer100g()).isEqualTo(0.5);
    }

    @Test
    @DisplayName("updateById: считает caloriesPer100g в ответе (Б*4 + Ж*9 + У*4)")
    void shouldReturnCalculatedCaloriesInResponse() {
        Ingredient existing = ingredient(5L, "Курица", "dmitriy", 20.0, 2.0, 1.0);
        when(ingredientRepository.findByIdAndUsername(5L, "dmitriy"))
                .thenReturn(Optional.of(existing));
        when(ingredientRepository.save(any(Ingredient.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        IngredientUpdateDTO dto = new IngredientUpdateDTO();
        dto.setProteinsPer100g(31.0);
        dto.setFatsPer100g(3.6);
        dto.setCarbsPer100g(0.0);

        IngredientResponseDto result = ingredientService.updateById(5L, dto, "dmitriy");

        // 31*4 + 3.6*9 + 0*4 = 124 + 32.4 = 156.4
        assertThat(result.getCaloriesPer100g()).isEqualTo(156.4, org.assertj.core.data.Offset.offset(0.001));
    }

    // ============================================================
    // updateById — ошибки
    // ============================================================

    @Test
    @DisplayName("updateById: не найден → ResourceNotFoundException")
    void shouldThrowWhenUpdatingMissing() {
        when(ingredientRepository.findByIdAndUsername(99L, "dmitriy"))
                .thenReturn(Optional.empty());

        IngredientUpdateDTO dto = new IngredientUpdateDTO();
        dto.setName("X");

        assertThatThrownBy(() -> ingredientService.updateById(99L, dto, "dmitriy"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99")
                .hasMessageContaining("dmitriy");

        verify(ingredientRepository, never()).save(any());
    }

    // ============================================================
    // deleteById
    // ============================================================

    @Test
    @DisplayName("deleteById: удаляет связи, потом сам ингредиент")
    void shouldDeleteRelationsThenIngredient() {
        Ingredient existing = ingredient(5L, "Курица", "dmitriy", 20.0, 2.0, 1.0);
        when(ingredientRepository.findByIdAndUsername(5L, "dmitriy"))
                .thenReturn(Optional.of(existing));

        ingredientService.deleteById(5L, "dmitriy");

        var inOrder = inOrder(recipeIngredientRepository, ingredientRepository);
        inOrder.verify(recipeIngredientRepository).deleteByIngredientId(5L);
        inOrder.verify(ingredientRepository).delete(existing);
    }

    @Test
    @DisplayName("deleteById: не найден → ResourceNotFoundException, репозитории не трогаем")
    void shouldThrowWhenDeletingMissing() {
        when(ingredientRepository.findByIdAndUsername(99L, "dmitriy"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.deleteById(99L, "dmitriy"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verifyNoInteractions(recipeIngredientRepository);
        verify(ingredientRepository, never()).delete(any());
    }
}