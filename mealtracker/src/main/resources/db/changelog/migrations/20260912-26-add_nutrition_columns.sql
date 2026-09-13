-- liquibase formatted sql
-- changeset dmitriy:add_nutrition_columns

ALTER TABLE recipes ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE recipes ADD COLUMN IF NOT EXISTS image_url VARCHAR(512);
ALTER TABLE recipes ADD COLUMN IF NOT EXISTS total_calories DECIMAL(10,2);
ALTER TABLE recipes ADD COLUMN IF NOT EXISTS total_proteins DECIMAL(10,2);
ALTER TABLE recipes ADD COLUMN IF NOT EXISTS total_fats DECIMAL(10,2);
ALTER TABLE recipes ADD COLUMN IF NOT EXISTS total_carbs DECIMAL(10,2);
