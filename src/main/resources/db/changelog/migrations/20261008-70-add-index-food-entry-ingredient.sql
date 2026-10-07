--liquibase formatted sql

--changeset dmitriy:20261008-70-add-index-food-entry-ingredient
CREATE INDEX IF NOT EXISTS idx_food_entries_ingredient
    ON food_entries(ingredient_id);
--rollback DROP INDEX IF EXISTS idx_food_entries_ingredient;