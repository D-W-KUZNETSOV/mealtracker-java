--liquibase formatted sql
--changeset dmitriy:20260902-05-create-meal-recipes-table-v1 runInTransaction:false
CREATE TABLE IF NOT EXISTS meal_recipes (
    id BIGSERIAL PRIMARY KEY,
    meal_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    portion_weight_grams DOUBLE PRECISION NOT NULL
);


