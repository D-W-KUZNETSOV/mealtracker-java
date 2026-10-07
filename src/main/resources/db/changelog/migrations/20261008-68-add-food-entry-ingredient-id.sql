--liquibase formatted sql

--changeset dmitriy:20261008-68-add-food-entry-ingredient-id
ALTER TABLE food_entries ADD COLUMN IF NOT EXISTS ingredient_id BIGINT;
--rollback ALTER TABLE food_entries DROP COLUMN IF EXISTS ingredient_id;