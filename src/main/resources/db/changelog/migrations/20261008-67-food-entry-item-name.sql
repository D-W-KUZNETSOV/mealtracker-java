--liquibase formatted sql

--changeset dmitriy:20261008-67-rename-food-entry-recipe-name
ALTER TABLE food_entries RENAME COLUMN recipe_name TO item_name;
--rollback ALTER TABLE food_entries RENAME COLUMN item_name TO recipe_name;





