-- liquibase formatted sql
-- changeset dmitriy:34-add_visibility_to_recipes
ALTER TABLE recipes
    ADD COLUMN visibility VARCHAR(50) NOT NULL DEFAULT 'PRIVATE';

CREATE INDEX IF NOT EXISTS idx_recipes_visibility ON recipes(visibility);