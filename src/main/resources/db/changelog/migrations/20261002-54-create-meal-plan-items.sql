--liquibase formatted sql
--changeset dmitriy:20261002-54-create-meal-plan-items
CREATE TABLE IF NOT EXISTS meal_plan_items (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    plan_id BIGINT NOT NULL,
    plan_date DATE NOT NULL,
    meal_type VARCHAR(50) NOT NULL,
    recipe_id BIGINT,
    ingredient_id BIGINT,
    servings DOUBLE PRECISION,
    weight_in_grams DOUBLE PRECISION,
    custom_name VARCHAR(255),
    CONSTRAINT fk_meal_plan_items_plan FOREIGN KEY (plan_id)
        REFERENCES meal_plans(id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_plan_items_recipe FOREIGN KEY (recipe_id)
        REFERENCES recipes(id) ON DELETE SET NULL,
    CONSTRAINT fk_meal_plan_items_ingredient FOREIGN KEY (ingredient_id)
        REFERENCES ingredients(id) ON DELETE SET NULL
);
--rollback DROP TABLE IF EXISTS meal_plan_items;