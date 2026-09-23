-- liquibase formatted sql
-- changeset dmitriy:20260902-08-add-category-to-recipes runInTransaction:false
ALTER TABLE recipes
ADD COLUMN category VARCHAR(255);
