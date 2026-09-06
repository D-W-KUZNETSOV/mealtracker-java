package com.e.mealtracker.config;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeIngredient;
import com.e.mealtracker.repository.IngredientRepository;
import com.e.mealtracker.repository.RecipeIngredientRepository;
import com.e.mealtracker.repository.RecipeRepository;
import com.e.mealtracker.service.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final Environment env;
    private final UserContextService userContextService;

    // TODO: [CONFIG] Вынести значение по умолчанию в application.yml как app.demo.default-username
    private static final String DEFAULT_DEMO_USER = "dmitriy";

    @Override
    @Transactional
    public void run(String... args) {
        boolean init = Boolean.parseBoolean(env.getProperty("app.init-demo-data", "false"));
        if (!init) {
            log.info("Demo data initialization is disabled. Set app.init-demo-data=true to enable.");
            return;
        }

        // ✅ Получаем текущего пользователя
        String currentUser = userContextService.getCurrentUsername();
        if (currentUser == null) {
            log.warn("No authenticated user found. Using default demo user: {}", DEFAULT_DEMO_USER);
            currentUser = DEFAULT_DEMO_USER;
        }

        // ✅ Логируем реального пользователя
        log.info("Starting demo data initialization for user: {}", currentUser);

        // ✅ Передаем username во все методы
        List<ProductData> baseProducts = getBaseProducts();
        for (ProductData pd : baseProducts) {
            ensureIngredient(pd.name, pd.fats, pd.proteins, pd.carbs, currentUser);
        }

        createOrUpdateRecipe(currentUser);

        log.info("Demo data initialization completed for user: {}", currentUser);
    }

    // ✅ Вынес список продуктов в отдельный метод
    private List<ProductData> getBaseProducts() {
        return List.of(
                new ProductData("Куриная грудка", 1.5, 31.0, 0.0),
                new ProductData("Гречка варёная", 1.5, 4.2, 28.7),
                new ProductData("Яйцо куриное", 11.5, 12.7, 0.7),
                new ProductData("Творог 5%", 5.0, 17.0, 3.0),
                new ProductData("Огурец свежий", 0.1, 0.8, 2.8),
                new ProductData("Рис варёный", 0.3, 2.7, 28.0),
                new ProductData("Овсянка на воде", 1.7, 3.0, 15.0),
                new ProductData("Молоко 3.2%", 3.6, 3.2, 4.8)
        );
    }

    // ✅ Добавлен параметр username
    private void ensureIngredient(String name, double fats, double proteins, double carbs, String username) {
        Optional<Ingredient> existing = ingredientRepository
                .findByNameIgnoreCaseAndUsername(name, username);

        if (existing.isPresent()) {
            Ingredient ing = existing.get();
            boolean updated = false;

            // ✅ Используем Double.compare с допуском
            if (Double.compare(getSafeDouble(ing.getFatsPer100g()), fats) != 0) {
                ing.setFatsPer100g(fats);
                updated = true;
            }
            if (Double.compare(getSafeDouble(ing.getProteinsPer100g()), proteins) != 0) {
                ing.setProteinsPer100g(proteins);
                updated = true;
            }
            if (Double.compare(getSafeDouble(ing.getCarbsPer100g()), carbs) != 0) {
                ing.setCarbsPer100g(carbs);
                updated = true;
            }

            if (updated) {
                ing.setCaloriesPer100g(ing.calculateCaloriesPer100g());
                ingredientRepository.save(ing);  // ✅ Сохраняем ТОЛЬКО при изменении
                log.info("Updated ingredient: {} for user {}", name, username);
            } else {
                log.debug("Ingredient already up to date: {}", name);
                // ❌ НЕ вызываем save здесь!
            }
        } else {
            Ingredient newIng = createIngredient(name, fats, proteins, carbs, username);
            ingredientRepository.save(newIng);
            log.info("Added ingredient: {} for user {}", name, username);
        }
    }

    // ✅ Вспомогательный метод для безопасного получения Double
    private double getSafeDouble(Double value) {
        return value != null ? value : 0.0;
    }

    // ✅ Добавлен параметр username
    private Ingredient createIngredient(String name, double fats, double proteins, double carbs, String username) {
        Ingredient i = new Ingredient();
        i.setName(name);
        i.setUsername(username);
        i.setFatsPer100g(fats);
        i.setProteinsPer100g(proteins);
        i.setCarbsPer100g(carbs);
        i.setCaloriesPer100g(i.calculateCaloriesPer100g());
        return i;
    }

    // ✅ Добавлен параметр username
    private void createOrUpdateRecipe(String username) {
        String recipeName = "Обед: курица + гречка";

        Optional<Recipe> existingRecipe = recipeRepository.findByNameAndUsername(recipeName, username);
        Recipe lunch = existingRecipe.orElseGet(() -> {
            Recipe newRecipe = new Recipe();
            newRecipe.setName(recipeName);
            newRecipe.setUsername(username);
            log.info("Creating new recipe: {}", recipeName);
            return recipeRepository.save(newRecipe);
        });

        Ingredient chicken = ingredientRepository
                .findByNameIgnoreCaseAndUsername("Куриная грудка", username)
                .orElseThrow(() -> new IllegalStateException(
                        "Ingredient 'Куриная грудка' not found for user " + username));

        Ingredient buckwheat = ingredientRepository
                .findByNameIgnoreCaseAndUsername("Гречка варёная", username)
                .orElseThrow(() -> new IllegalStateException(
                        "Ingredient 'Гречка варёная' not found for user " + username));

        // TODO: [CODE] Заменить магические числа на константы
        ensureRecipeIngredient(lunch, chicken, 200.0);
        ensureRecipeIngredient(lunch, buckwheat, 150.0);

        log.info("Recipe '{}' has {} ingredients", recipeName,
                recipeIngredientRepository.countByRecipe(lunch));
    }

    private void ensureRecipeIngredient(Recipe recipe, Ingredient ingredient, double weightInGrams) {
        // TODO: [PERFORMANCE] Использовать existsByRecipeAndIngredient вместо count
        long count = recipeIngredientRepository.countByRecipeAndIngredient(recipe, ingredient);
        if (count == 0) {
            RecipeIngredient ri = new RecipeIngredient();
            ri.setRecipe(recipe);
            ri.setIngredient(ingredient);
            ri.setWeightInGrams(weightInGrams);
            recipeIngredientRepository.save(ri);
            log.debug("Added ingredient '{}' ({}g) to recipe '{}'",
                    ingredient.getName(), weightInGrams, recipe.getName());
        } else {
            // TODO: [FEATURE] Добавить обновление веса ингредиента в рецепте
            log.debug("Ingredient '{}' already in recipe '{}'",
                    ingredient.getName(), recipe.getName());
        }
    }

    // TODO: [ARCH] Вынести ProductData в отдельный DTO класс
    private record ProductData(String name, double fats, double proteins, double carbs) {}
}
