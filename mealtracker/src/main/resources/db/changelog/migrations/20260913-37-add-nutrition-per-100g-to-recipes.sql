-- liquibase formatted sql
-- changeset dmitriy:37-add-nutrition-per-100g-to-recipes

ALTER TABLE recipes ADD COLUMN calories_per_100g NUMERIC(10,2) DEFAULT 0;
ALTER TABLE recipes ADD COLUMN protein_per_100g  NUMERIC(10,2) DEFAULT 0;
ALTER TABLE recipes ADD COLUMN fat_per_100g      NUMERIC(10,2) DEFAULT 0;
ALTER TABLE recipes ADD COLUMN carbs_per_100g    NUMERIC(10,2) DEFAULT 0;