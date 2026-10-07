--liquibase formatted sql

--changeset dmitriy:2026-10-07-add-original-recipe-id
ALTER TABLE recipes ADD COLUMN IF NOT EXISTS original_recipe_id BIGINT;
--rollback ALTER TABLE recipes DROP COLUMN IF EXISTS original_recipe_id;

--changeset dmitriy:2026-10-07-add-index-original-recipe
CREATE INDEX IF NOT EXISTS idx_recipes_original_recipe
    ON recipes(original_recipe_id);
--rollback DROP INDEX IF EXISTS idx_recipes_original_recipe;