-- liquibase formatted sql
-- changeset dmitriy:20260905-12-add_username_to_recipes
ALTER TABLE recipes ADD COLUMN username VARCHAR(255) NOT NULL DEFAULT 'unknown';
