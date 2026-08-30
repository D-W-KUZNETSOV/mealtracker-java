package com.e.mealtracker.config;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final IngredientRepository ingredientRepository;
    private final Environment env;

    @Override
    public void run(String... args) {
        boolean init = Boolean.parseBoolean(env.getProperty("app.init-demo-data", "false"));
        if (!init) {
            return;
        }

        if (ingredientRepository.count() == 0) {
            ingredientRepository.saveAll(List.of(
                    createIngredient("Куриная грудка", 2.5, 23.0, 0.0),
                    createIngredient("Гречка варёная", 0.6, 3.4, 19.9),
                    createIngredient("Оливковое масло", 99.9, 0.0, 0.0)
            ));
            System.out.println("Demo data initialized with macros.");
        }
    }

    private Ingredient createIngredient(String name, double fats, double proteins, double carbs) {
        Ingredient i = new Ingredient();
        i.setName(name);
        i.setFatsPer100g(fats);
        i.setProteinsPer100g(proteins);
        i.setCarbsPer100g(carbs);

        // Опционально: сохраняем рассчитанные калории, чтобы не было NULL
        double cal = i.calculateCaloriesPer100g();
        i.setCaloriesPer100g(cal);

        return i;
    }
}

