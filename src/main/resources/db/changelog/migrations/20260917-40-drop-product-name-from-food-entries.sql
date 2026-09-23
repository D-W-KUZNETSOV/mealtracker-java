-- liquibase formatted sql
-- changeset dmitriy:40-drop-product-name-from-food-entries
ALTER TABLE food_entries DROP COLUMN IF EXISTS product_name;
-- rollback ALTER TABLE food_entries ADD COLUMN product_name VARCHAR(200);