-- liquibase formatted sql
-- changeset dmitriy:20260905-14-fix_ingredients_unique_index
ALTER TABLE ingredients DROP CONSTRAINT IF EXISTS ingredients_name_key;
CREATE UNIQUE INDEX IF NOT EXISTS ingredients_username_name_key ON ingredients (username, name);
