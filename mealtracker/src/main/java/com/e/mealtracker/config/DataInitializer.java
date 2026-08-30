package com.e.mealtracker.config;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeIngredient;
import com.e.mealtracker.repository.IngredientRepository;
import com.e.mealtracker.repository.RecipeIngredientRepository;
import com.e.mealtracker.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final Environment env;

    @Override
    public void run(String... args) {
        boolean init = Boolean.parseBoolean(env.getProperty("app.init-demo-data", "false"));
        if (!init) {
            return;
        }

        // Базовый набор продуктов: name, fats, proteins, carbs (на 100 г)
        List<ProductData> baseProducts = List.of(
                new ProductData("Куриная грудка", 5.0, 25.0, 0.0),
                new ProductData("Гречка варёная", 1.5, 4.2, 28.7),
                new ProductData("Яйцо куриное (1 шт)", 5.3, 6.3, 0.7),
                new ProductData("Творог 5%", 5.0, 17.0, 3.0),
                new ProductData("Огурец свежий", 0.1, 0.8, 2.8)
        );

        for (ProductData pd : baseProducts) {
            ensureIngredient(pd.name, pd.fats, pd.proteins, pd.carbs);
        }

        // Создаём тестовый рецепт «Обед: курица + гречка», если его нет
        Recipe lunch = recipeRepository.findByName("Обед: курица + гречка")
                .orElseGet(() -> {
                    Recipe r = new Recipe();
                    r.setName("Обед: курица + гречка");
                    return recipeRepository.save(r);
                });

        Ingredient chicken = ingredientRepository.findByName("Куриная грудка").orElseThrow();
        Ingredient buckwheat = ingredientRepository.findByName("Гречка варёная").orElseThrow();

        ensureRecipeIngredient(lunch, chicken, 200.0); // 200 г курицы
        ensureRecipeIngredient(lunch, buckwheat, 150.0); // 150 г гречки

        System.out.println("Demo data initialized with ingredients, recipe, and links.");
    }

    /**
     * Добавляет ингредиент, если его нет, или дозаполняет КБЖУ, если они null.
     */
    private void ensureIngredient(String name, double fats, double proteins, double carbs) {
        Optional<Ingredient> existing = ingredientRepository.findByName(name);

        if (existing.isPresent()) {
            Ingredient ing = existing.get();
            boolean updated = false;

            if (ing.getFatsPer100g() == null) {
                ing.setFatsPer100g(fats);
                updated = true;
            }
            if (ing.getProteinsPer100g() == null) {
                ing.setProteinsPer100g(proteins);
                updated = true;
            }
            if (ing.getCarbsPer100g() == null) {
                ing.setCarbsPer100g(carbs);
                updated = true;
            }

            // Пересчитываем калории, если обновили макросы
            if (updated) {
                ing.setCaloriesPer100g(ing.calculateCaloriesPer100g());
                ingredientRepository.save(ing);
            }
        } else {
            Ingredient newIng = createIngredient(name, fats, proteins, carbs);
            ingredientRepository.save(newIng);
        }
    }

    private Ingredient createIngredient(String name, double fats, double proteins, double carbs) {
        Ingredient i = new Ingredient();
        i.setName(name);
        i.setFatsPer100g(fats);
        i.setProteinsPer100g(proteins);
        i.setCarbsPer100g(carbs);
        i.setCaloriesPer100g(i.calculateCaloriesPer100g());
        return i;
    }

    private void ensureRecipeIngredient(Recipe recipe, Ingredient ingredient, double weightInGrams) {
        // Проверяем, нет ли уже такой связи
        long count = recipeIngredientRepository.countByRecipeIdAndIngredientId(recipe.getId(), ingredient.getId());
        if (count == 0) {
            RecipeIngredient ri = new RecipeIngredient();
            ri.setRecipe(recipe);
            ri.setIngredient(ingredient);
            ri.setWeightInGrams(weightInGrams);
            recipeIngredientRepository.save(ri);
        }
    }

    // Вспомогательный класс для передачи данных о продукте
    private record ProductData(String name, double fats, double proteins, double carbs) {}
}

