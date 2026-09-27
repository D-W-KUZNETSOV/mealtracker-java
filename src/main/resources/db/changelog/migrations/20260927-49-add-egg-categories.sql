-- liquibase formatted sql
-- changeset dmitriy:20260927-49-add-egg-categories

-- ============================================================
-- ФИКС: Яйца — удаление дубля + категории С0/С1/С2
-- ============================================================
-- 1. Удалить дубль "Яйцо куриное (сырое)"
-- 2. Переименовать "Яйцо куриное" → "Яйцо куриное (среднее)"
-- 3. Добавить 3 категории: С0 (70 г), С1 (60 г), С2 (50 г)
-- ============================================================

-- 1. Удалить дубль
DELETE FROM ingredients
WHERE name = 'Яйцо куриное (сырое)'
  AND username = 'SYSTEM';

-- 2. Переименовать базовое яйцо
UPDATE ingredients
SET name = 'Яйцо куриное (среднее)',
    unit_weight_grams = 55.0
WHERE name = 'Яйцо куриное'
  AND username = 'SYSTEM';

-- 3. Добавить категории (idempotent)
INSERT INTO ingredients
  (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, unit_type, unit_weight_grams)
SELECT 'Яйцо куриное С0 (крупное)', 'SYSTEM', 157.1, 12.7, 11.5, 0.7, 'PIECE', 70.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Яйцо куриное С0 (крупное)' AND username = 'SYSTEM');

INSERT INTO ingredients
  (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, unit_type, unit_weight_grams)
SELECT 'Яйцо куриное С1 (среднее)', 'SYSTEM', 157.1, 12.7, 11.5, 0.7, 'PIECE', 60.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Яйцо куриное С1 (среднее)' AND username = 'SYSTEM');

INSERT INTO ingredients
  (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, unit_type, unit_weight_grams)
SELECT 'Яйцо куриное С2 (мелкое)', 'SYSTEM', 157.1, 12.7, 11.5, 0.7, 'PIECE', 50.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Яйцо куриное С2 (мелкое)' AND username = 'SYSTEM');

-- ============================================================
-- ROLLBACK
-- ============================================================
-- rollback INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, unit_type, unit_weight_grams) VALUES ('Яйцо куриное (сырое)', 'SYSTEM', 155.0, 12.7, 11.5, 0.7, 'PIECE', 55.0);
-- rollback UPDATE ingredients SET name = 'Яйцо куриное' WHERE name = 'Яйцо куриное (среднее)' AND username = 'SYSTEM';
-- rollback DELETE FROM ingredients WHERE name IN ('Яйцо куриное С0 (крупное)', 'Яйцо куриное С1 (среднее)', 'Яйцо куриное С2 (мелкое)') AND username = 'SYSTEM';