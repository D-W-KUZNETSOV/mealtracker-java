--liquibase formatted sql
--changeset dmitriy:20260902-04-create-recipe-ingredients-table-v1 runInTransaction:false
CREATE TABLE IF NOT EXISTS recipe_ingredients (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    weight_in_grams DOUBLE PRECISION NOT NULL
);

