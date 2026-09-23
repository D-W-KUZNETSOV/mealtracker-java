package com.e.mealtracker.config;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeIngredient;
import com.e.mealtracker.domain.RecipeVisibility;
import com.e.mealtracker.entity.Role;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.entity.UserProfile;
import com.e.mealtracker.repository.*;
import com.e.mealtracker.util.ActivityLevel;
import com.e.mealtracker.util.Gender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final UserRepository userRepository;
    private final Environment env;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.demo.default-username:dmitriy}")
    private String defaultDemoUsername = "dmitriy";  // ← Java-дефолт на случай, если @Value не сработает

    @Value("${app.demo.default-password:demo}")
    private String demoPassword = "demo";

    private String resolveUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return auth.getName();
    }

    @Override
    @Transactional
    public void run(String... args) {
        boolean init = Boolean.parseBoolean(env.getProperty("app.init-demo-data", "false"));
        if (!init) {
            log.info("Demo data initialization is disabled.");
            return;
        }

        String currentUsername = resolveUsername();
        if (currentUsername == null || currentUsername.isBlank()) {
            currentUsername = defaultDemoUsername;
        }

        log.info("Starting demo data initialization for user: {}", currentUsername);

        final String finalUsername = currentUsername;

        User user = userRepository.findByUsername(finalUsername)
                .orElseGet(() -> {
                    log.warn("User '{}' not found. Creating demo user.", finalUsername);
                    User newUser = new User();
                    newUser.setUsername(finalUsername);
                    newUser.setPassword(passwordEncoder.encode(demoPassword));
                    newUser.setRole(Role.USER);
                    newUser.setEmail("torgor_8@mail.ru");

                    UserProfile profile = new UserProfile();
                    profile.setHeightCm(178);
                    profile.setTargetWeightKg(new BigDecimal("75.0"));
                    profile.setCurrentWeightKg(new BigDecimal("81.0"));
                    profile.setGender(Gender.MALE);
                    profile.setActivityLevel(ActivityLevel.MODERATE);
                    profile.setDateOfBirth(LocalDate.of(1995, 5, 20));

                    newUser.setProfile(profile);
                    return userRepository.save(newUser);
                });

        createOrUpdateRecipe(user);
        log.info("Demo data initialization completed for user: {}", user.getUsername());
    }

    private void createOrUpdateRecipe(User user) {
        String recipeName = "Обед: курица + гречка";

        Recipe lunch = recipeRepository.findByNameAndUser(recipeName, user)
                .orElseGet(() -> {
                    Recipe newRecipe = new Recipe();
                    newRecipe.setName(recipeName);
                    newRecipe.setUser(user);
                    newRecipe.setVisibility(RecipeVisibility.PUBLIC);
                    log.info("Creating new recipe: {}", recipeName);
                    return recipeRepository.save(newRecipe);
                });

        Optional<Ingredient> chickenOpt = ingredientRepository
                .findByNameIgnoreCaseAndUsername("Куриная грудка", Ingredient.SYSTEM_USERNAME);
        Optional<Ingredient> buckwheatOpt = ingredientRepository
                .findByNameIgnoreCaseAndUsername("Гречка варёная", Ingredient.SYSTEM_USERNAME);

        if (chickenOpt.isEmpty() || buckwheatOpt.isEmpty()) {
            log.warn("Required ingredients not found, skipping recipe creation.");
            return;
        }

        ensureRecipeIngredient(lunch, chickenOpt.get(), 200.0);
        ensureRecipeIngredient(lunch, buckwheatOpt.get(), 150.0);

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


