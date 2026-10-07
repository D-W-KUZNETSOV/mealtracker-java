--liquibase formatted sql

--changeset dmitriy:20261008-69-add-fk-food-entry-ingredient
ALTER TABLE food_entries DROP CONSTRAINT IF EXISTS fk_food_entries_ingredient;
ALTER TABLE food_entries ADD CONSTRAINT fk_food_entries_ingredient
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE SET NULL;
--rollback ALTER TABLE food_entries DROP CONSTRAINT IF EXISTS fk_food_entries_ingredient;