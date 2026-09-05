-- liquibase formatted sql
-- changeset dmitriy:20260905-15-drop_ingredients_name_constraint
ALTER TABLE ingredients DROP CONSTRAINT IF EXISTS ingredients_name_key;
CREATE UNIQUE INDEX IF NOT EXISTS ingredients_username_name_key ON ingredients (username, name);
