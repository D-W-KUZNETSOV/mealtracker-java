-- liquibase formatted sql
-- changeset dmitriy:20260905-17-add_username_to_recipes

ALTER TABLE recipes ADD COLUMN username VARCHAR(255);

-- Заполни существующим пользователям (если есть старые рецепты)
UPDATE recipes SET username = 'dmitriy' WHERE username IS NULL;

ALTER TABLE recipes ALTER COLUMN username SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_recipes_username ON recipes (username);
