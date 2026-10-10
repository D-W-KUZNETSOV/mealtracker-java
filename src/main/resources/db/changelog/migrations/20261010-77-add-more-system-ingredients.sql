--liquibase formatted sql

--changeset dmitriy:20261010-77-add-more-system-ingredients
-- Добавление: курица (бедро сырое), фасоль стручковая, 8 видов сыра

-- ============================================================
-- КУРИЦА
-- ============================================================
INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Куриное бедро (сырое)', 'куриное бедро (сырое)', 'SYSTEM', 185, 21, 11, 0, 'MEAT'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Куриное бедро (сырое)' AND username = 'SYSTEM');

-- ============================================================
-- ОВОЩИ
-- ============================================================
INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Фасоль стручковая', 'фасоль стручковая', 'SYSTEM', 31, 2.5, 0.3, 3, 'VEGETABLES'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Фасоль стручковая' AND username = 'SYSTEM');

-- ============================================================
-- СЫРЫ (8 видов)
-- ============================================================
INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Пармезан', 'пармезан', 'SYSTEM', 392, 33, 28, 0, 'DAIRY'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Пармезан' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Фета', 'фета', 'SYSTEM', 264, 14, 21, 4, 'DAIRY'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Фета' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Сулугуни', 'сулугуни', 'SYSTEM', 290, 20, 22, 0, 'DAIRY'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Сулугуни' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Брынза', 'брынза', 'SYSTEM', 260, 17, 20, 0, 'DAIRY'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Брынза' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Гауда', 'гауда', 'SYSTEM', 356, 25, 27, 2.2, 'DAIRY'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Гауда' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Сыр российский', 'сыр российский', 'SYSTEM', 364, 23, 30, 0, 'DAIRY'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Сыр российский' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Адыгейский сыр', 'адыгейский сыр', 'SYSTEM', 240, 19, 14, 3, 'DAIRY'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Адыгейский сыр' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Рикотта', 'рикотта', 'SYSTEM', 174, 11, 13, 3, 'DAIRY'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Рикотта' AND username = 'SYSTEM');

-- ============================================================
-- ROLLBACK
-- ============================================================
-- rollback DELETE FROM ingredients WHERE username = 'SYSTEM' AND name IN (
--   'Куриное бедро (сырое)', 'Фасоль стручковая',
--   'Пармезан', 'Фета', 'Сулугуни', 'Брынза', 'Гауда', 'Сыр российский', 'Адыгейский сыр', 'Рикотта'
-- );