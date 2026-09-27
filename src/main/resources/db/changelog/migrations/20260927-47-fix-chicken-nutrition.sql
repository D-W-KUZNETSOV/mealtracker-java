-- liquibase formatted sql
-- changeset dmitriy:20260927-47-fix-chicken-nutrition

-- ============================================================
-- ФИКС: Курица, ложки, дубли
-- ============================================================
-- Все изменения уже применены вручную через DBeaver.
-- Эта миграция нужна для развёртывания с нуля / новых БД.
-- Idempotent — повторный запуск не сломает данные.
-- ============================================================

-- ============================================================
-- КУРИЦА: правильные КБЖУ
-- ============================================================

UPDATE ingredients
SET calories_per100g = 113.0,
    proteins_per100g = 21.6,
    fats_per100g = 2.5,
    carbs_per100g = 0.0
WHERE name = 'Куриная грудка (сырая)'
  AND username = 'SYSTEM';

UPDATE ingredients
SET name = 'Куриная грудка тушёная',
    calories_per100g = 150.0,
    proteins_per100g = 28.0,
    fats_per100g = 3.0,
    carbs_per100g = 0.0
WHERE name = 'Куриная грудка'
  AND username = 'SYSTEM';

UPDATE ingredients
SET name = 'Куриное бедро варёное'
WHERE name = 'Куриное бедро'
  AND username = 'SYSTEM';

-- ============================================================
-- НОВЫЕ ВАРИАНТЫ КУРИЦЫ
-- ============================================================

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, unit_type)
SELECT 'Куриная грудка гриль', 'SYSTEM', 165.0, 31.0, 3.6, 0.0, 'GRAM'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Куриная грудка гриль' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, unit_type)
SELECT 'Куриная грудка жареная', 'SYSTEM', 210.0, 28.0, 10.0, 1.0, 'GRAM'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Куриная грудка жареная' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, unit_type)
SELECT 'Куриное бедро гриль', 'SYSTEM', 209.0, 22.0, 13.0, 0.0, 'GRAM'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Куриное бедро гриль' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, unit_type)
SELECT 'Куриное бедро жареное', 'SYSTEM', 250.0, 21.0, 18.0, 1.0, 'GRAM'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Куриное бедро жареное' AND username = 'SYSTEM');

-- ============================================================
-- ЛОЖКИ: правильные веса
-- ============================================================

UPDATE ingredients
SET unit_weight_grams = 15.0
WHERE name = 'Масло сливочное'
  AND username = 'SYSTEM';

UPDATE ingredients
SET unit_weight_grams = 7.0
WHERE name = 'Соль'
  AND username = 'SYSTEM';

-- ============================================================
-- УДАЛЕНИЕ ДУБЛЕЙ
-- ============================================================

DELETE FROM ingredients
WHERE name IN (
    'Рис (сухой)',
    'Сахар белый',
    'Масло сливочное 82.5%',
    'Овсяные хлопья'
)
AND username = 'SYSTEM';

-- ============================================================
-- ROLLBACK
-- ============================================================
-- rollback UPDATE ingredients SET calories_per100g = 165.0, proteins_per100g = 31.0, fats_per100g = 3.6 WHERE name = 'Куриная грудка (сырая)' AND username = 'SYSTEM';
-- rollback UPDATE ingredients SET name = 'Куриная грудка', calories_per100g = 140.0, proteins_per100g = 31.0, fats_per100g = 1.5 WHERE name = 'Куриная грудка тушёная' AND username = 'SYSTEM';
-- rollback UPDATE ingredients SET name = 'Куриное бедро' WHERE name = 'Куриное бедро варёное' AND username = 'SYSTEM';
-- rollback DELETE FROM ingredients WHERE name IN ('Куриная грудка гриль', 'Куриная грудка жареная', 'Куриное бедро гриль', 'Куриное бедро жареное') AND username = 'SYSTEM';
-- rollback UPDATE ingredients SET unit_weight_grams = 20.0 WHERE name = 'Масло сливочное' AND username = 'SYSTEM';
-- rollback UPDATE ingredients SET unit_weight_grams = 10.0 WHERE name = 'Соль' AND username = 'SYSTEM';