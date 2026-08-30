-- liquibase formatted sql

-- changeset you:add_macros_to_ingredients
ALTER TABLE ingredients
    ADD COLUMN fats_per100g DOUBLE PRECISION,
    ADD COLUMN proteins_per100g DOUBLE PRECISION,
    ADD COLUMN carbs_per100g DOUBLE PRECISION;
