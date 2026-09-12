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

    private static final String DEFAULT_DEMO_USER = "dmitriy";

    @Override
    @Transactional
    public void run(String... args) {
        boolean init = Boolean.parseBoolean(env.getProperty("app.init-demo-data", "false"));
        if (!init) {
            log.info("Demo data initialization is disabled.");
            return;
        }

        String currentUser = userContextService.getCurrentUsername();
        if (currentUser == null) {
            currentUser = DEFAULT_DEMO_USER;
        }

        log.info("Starting demo data initialization for user: {}", currentUser);
        createOrUpdateRecipe(currentUser);
        log.info("Demo data initialization completed for user: {}", currentUser);
    }

    private void createOrUpdateRecipe(String username) {
        String recipeName = "Обед: курица + гречка";

        Recipe lunch = recipeRepository.findByNameAndUsername(recipeName, username)
                .orElseGet(() -> {
                    Recipe newRecipe = new Recipe();
                    newRecipe.setName(recipeName);
                    newRecipe.setUsername(username);
                    log.info("Creating new recipe: {}", recipeName);
                    return recipeRepository.save(newRecipe);
                });

        // Ищем в общем справочнике (username = NULL)
        Ingredient chicken = ingredientRepository
                .findByNameIgnoreCaseAndUsernameIsNull("Куриная грудка")
                .orElseThrow(() -> new IllegalStateException(
                        "Ingredient 'Куриная грудка' not found in shared catalog"));

        Ingredient buckwheat = ingredientRepository
                .findByNameIgnoreCaseAndUsernameIsNull("Гречка варёная")
                .orElseThrow(() -> new IllegalStateException(
                        "Ingredient 'Гречка варёная' not found in shared catalog"));


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
}

