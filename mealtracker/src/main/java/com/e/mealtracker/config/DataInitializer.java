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
        // Флаг включения демо‑данных из application.properties
        boolean init = Boolean.parseBoolean(env.getProperty("app.init-demo-data", "false"));
        if (!init) {
            return;
        }

        if (ingredientRepository.count() == 0) {
            ingredientRepository.saveAll(List.of(
                    createIngredient("Куриная грудка", 165.0),
                    createIngredient("Гречка", 335.0),
                    createIngredient("Оливковое масло", 899.0)
            ));
            System.out.println("Demo data initialized.");
        }
    }

    // Фабричный метод: создаёт и заполняет объект — это и есть «правильный» подход,
    // когда не хочется зависеть от авто‑конструкторов Lombok
    private Ingredient createIngredient(String name, double calories) {
        Ingredient i = new Ingredient();      // есть благодаря @NoArgsConstructor
        i.setName(name);                     // есть благодаря @Data
        i.setCaloriesPer100g(calories);      // есть благодаря @Data
        return i;
    }
}

