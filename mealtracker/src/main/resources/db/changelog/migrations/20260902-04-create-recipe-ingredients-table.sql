--liquibase formatted sql
--changeset dmitriy:20260902-04-create-recipe-ingredients-table-v1
CREATE TABLE recipe_ingredients (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    weight_in_grams DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_recipe_ingredients_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id),
    CONSTRAINT fk_recipe_ingredients_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);
--rollback
DROP TABLE IF EXISTS recipe_ingredients;
