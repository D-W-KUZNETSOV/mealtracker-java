-- liquibase formatted sql
-- changeset dmitriy:20260905-17-add_username_to_recipes

-- Заполняем существующие записи, если username пуст
UPDATE recipes SET username = 'dmitriy' WHERE username IS NULL;

CREATE INDEX IF NOT EXISTS idx_recipes_username ON recipes (username);


