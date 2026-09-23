-- liquibase formatted sql
-- changeset dmitriy:20260905-13-add_username_to_ingredients
ALTER TABLE ingredients ADD COLUMN username VARCHAR(255) NOT NULL DEFAULT 'unknown';

-- Меняем уникальный индекс: был unique(name), стал unique(username, name)
DROP INDEX IF EXISTS uk_ingredients_name;
CREATE UNIQUE INDEX uk_ingredients_username_name ON ingredients (username, name);
