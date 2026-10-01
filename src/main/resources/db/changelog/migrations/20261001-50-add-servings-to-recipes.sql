-- liquibase formatted sql

-- changeset dmitriy:20261001-50-add-servings-to-recipes
ALTER TABLE recipes ADD COLUMN servings INT NOT NULL DEFAULT 1;