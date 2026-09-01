--liquibase formatted sql
--changeset dmitriy:20260902-05-create-meal-recipes-table-v1
CREATE TABLE meal_recipes (
    id BIGSERIAL PRIMARY KEY,
    meal_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    portion_weight_grams DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_meal_recipes_meal FOREIGN KEY (meal_id) REFERENCES meals(id),
    CONSTRAINT fk_meal_recipes_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id)
);
--rollback
DROP TABLE IF EXISTS meal_recipes;
