package com.e.mealtracker.config;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeIngredient;
import com.e.mealtracker.repository.IngredientRepository;
import com.e.mealtracker.repository.RecipeIngredientRepository;
import com.e.mealtracker.repository.RecipeRepository;
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

    private static final String DEMO_USER = "dmitriy";

    @Override
    @Transactional
    public void run(String... args) {
        boolean init = Boolean.parseBoolean(env.getProperty("app.init-demo-data", "false"));
        if (!init) {
            log.info("Demo data initialization is disabled. Set app.init-demo-data=true to enable.");
            return;
        }

        log.info("Starting demo data initialization for user: {}", DEMO_USER);

        // Данные на 100 грамм продукта
        List<ProductData> baseProducts = List.of(
                new ProductData("Куриная грудка", 1.5, 31.0, 0.0),      // жиры, белки, углеводы на 100г
                new ProductData("Гречка варёная", 1.5, 4.2, 28.7),
                new ProductData("Яйцо куриное", 11.5, 12.7, 0.7),       // на 100г (≈2 яйца)
                new ProductData("Творог 5%", 5.0, 17.0, 3.0),
                new ProductData("Огурец свежий", 0.1, 0.8, 2.8),
                new ProductData("Рис варёный", 0.3, 2.7, 28.0),
                new ProductData("Овсянка на воде", 1.7, 3.0, 15.0),
                new ProductData("Молоко 3.2%", 3.6, 3.2, 4.8)
        );

        for (ProductData pd : baseProducts) {
            ensureIngredient(pd.name, pd.fats, pd.proteins, pd.carbs);
        }

        // Создаём тестовый рецепт
        createOrUpdateRecipe();

        log.info("Demo data initialization completed for user: {}", DEMO_USER);
    }

    private void ensureIngredient(String name, double fats, double proteins, double carbs) {
        Optional<Ingredient> existing = ingredientRepository
                .findByNameIgnoreCaseAndUsername(name, DEMO_USER);

        if (existing.isPresent()) {
            Ingredient ing = existing.get();
            boolean updated = false;

            // Обновляем только если данные изменились
            if (Double.compare(ing.getFatsPer100g() != null ? ing.getFatsPer100g() : 0.0, fats) != 0) {
                ing.setFatsPer100g(fats);
                updated = true;
            }
            if (Double.compare(ing.getProteinsPer100g() != null ? ing.getProteinsPer100g() : 0.0, proteins) != 0) {
                ing.setProteinsPer100g(proteins);
                updated = true;
            }
            if (Double.compare(ing.getCarbsPer100g() != null ? ing.getCarbsPer100g() : 0.0, carbs) != 0) {
                ing.setCarbsPer100g(carbs);
                updated = true;
            }

            if (updated) {
                ing.setCaloriesPer100g(ing.calculateCaloriesPer100g());
                ingredientRepository.save(ing);
                log.debug("Updated ingredient: {} for user {}", name, DEMO_USER);
            } else {
                log.debug("Ingredient already up to date: {}", name);
            }
        } else {
            Ingredient newIng = createIngredient(name, fats, proteins, carbs);
            ingredientRepository.save(newIng);
            log.info("Added ingredient: {} for user {}", name, DEMO_USER);
        }
    }

    private Ingredient createIngredient(String name, double fats, double proteins, double carbs) {
        Ingredient i = new Ingredient();
        i.setName(name);
        i.setUsername(DEMO_USER);
        i.setFatsPer100g(fats);
        i.setProteinsPer100g(proteins);
        i.setCarbsPer100g(carbs);
        i.setCaloriesPer100g(i.calculateCaloriesPer100g());
        return i;
    }

    private void createOrUpdateRecipe() {
        String recipeName = "Обед: курица + гречка";

        Optional<Recipe> existingRecipe = recipeRepository.findByNameAndUsername(recipeName, DEMO_USER);
        Recipe lunch = existingRecipe.orElseGet(() -> {
            Recipe newRecipe = new Recipe();
            newRecipe.setName(recipeName);
            newRecipe.setUsername(DEMO_USER);
            log.info("Creating new recipe: {}", recipeName);
            return recipeRepository.save(newRecipe);
        });

        // Получаем ингредиенты
        Ingredient chicken = ingredientRepository
                .findByNameIgnoreCaseAndUsername("Куриная грудка", DEMO_USER)
                .orElseThrow(() -> new IllegalStateException("Ingredient 'Куриная грудка' not found for user " + DEMO_USER));

        Ingredient buckwheat = ingredientRepository
                .findByNameIgnoreCaseAndUsername("Гречка варёная", DEMO_USER)
                .orElseThrow(() -> new IllegalStateException("Ingredient 'Гречка варёная' not found for user " + DEMO_USER));

        // Добавляем ингредиенты в рецепт (вес в граммах)
        ensureRecipeIngredient(lunch, chicken, 200.0);
        ensureRecipeIngredient(lunch, buckwheat, 150.0);

        log.info("Recipe '{}' has {} ingredients", recipeName,
                recipeIngredientRepository.countByRecipe(lunch));
    }

    private void ensureRecipeIngredient(Recipe recipe, Ingredient ingredient, double weightInGrams) {
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
            log.debug("Ingredient '{}' already in recipe '{}'",
                    ingredient.getName(), recipe.getName());
        }
    }

    private record ProductData(String name, double fats, double proteins, double carbs) {}
}

