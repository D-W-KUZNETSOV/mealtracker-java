--liquibase formatted sql
--changeset dmitriy:20260902-06-add-foreign-keys-v1 runInTransaction:false
ALTER TABLE recipe_ingredients
    ADD CONSTRAINT fk_recipe_ingredients_recipe
    FOREIGN KEY (recipe_id) REFERENCES recipes(id);

ALTER TABLE recipe_ingredients
    ADD CONSTRAINT fk_recipe_ingredients_ingredient
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id);

ALTER TABLE meal_recipes
    ADD CONSTRAINT fk_meal_recipes_meal
    FOREIGN KEY (meal_id) REFERENCES meals(id);

ALTER TABLE meal_recipes
    ADD CONSTRAINT fk_meal_recipes_recipe
    FOREIGN KEY (recipe_id) REFERENCES recipes(id);

--rollback
ALTER TABLE recipe_ingredients DROP CONSTRAINT IF EXISTS fk_recipe_ingredients_recipe;
ALTER TABLE recipe_ingredients DROP CONSTRAINT IF EXISTS fk_recipe_ingredients_ingredient;
ALTER TABLE meal_recipes DROP CONSTRAINT IF EXISTS fk_meal_recipes_meal;
ALTER TABLE meal_recipes DROP CONSTRAINT IF EXISTS fk_meal_recipes_recipe;