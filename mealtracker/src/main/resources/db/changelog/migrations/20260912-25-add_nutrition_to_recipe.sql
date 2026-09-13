-- liquibase formatted sql
-- changeset dmitriy:add_nutrition_to_recipe
ALTER TABLE recipes ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE recipes ADD COLUMN IF NOT EXISTS image_url VARCHAR(512);
ALTER TABLE recipes ADD COLUMN IF NOT EXISTS total_calories DECIMAL(10,2);
ALTER TABLE recipes ADD COLUMN IF NOT EXISTS total_proteins DECIMAL(10,2);
ALTER TABLE recipes ADD COLUMN IF NOT EXISTS total_fats DECIMAL(10,2);
ALTER TABLE recipes ADD COLUMN IF NOT EXISTS total_carbs DECIMAL(10,2);

-- rollback
ALTER TABLE recipes DROP COLUMN IF EXISTS description;
ALTER TABLE recipes DROP COLUMN IF EXISTS image_url;
ALTER TABLE recipes DROP COLUMN IF EXISTS total_calories;
ALTER TABLE recipes DROP COLUMN IF EXISTS total_proteins;
ALTER TABLE recipes DROP COLUMN IF EXISTS total_fats;
ALTER TABLE recipes DROP COLUMN IF EXISTS total_carbs;
