--liquibase formatted sql

--changeset dmitriy:20261009-73-seed-ingredients-from-dmitriy
-- Базовые ингредиенты из личного опыта Dmitriy (23 шт + 3 Bombbar)

-- ============================================================
-- МЯСО И ПТИЦА
-- ============================================================
INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Индейка (филе бедра)', 'индейка (филе бедра)', 'SYSTEM', 149, 19.2, 7.2, 0, 'MEAT'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Индейка (филе бедра)' AND username = 'SYSTEM');

-- ============================================================
-- РЫБА И МОРЕПРОДУКТЫ
-- ============================================================
INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Сардина консервированная', 'сардина консервированная', 'SYSTEM', 180, 22, 10, 0, 'FISH'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Сардина консервированная' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Сёмга', 'сёмга', 'SYSTEM', 208, 20, 13, 0, 'FISH'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Сёмга' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Тунец консервированный', 'тунец консервированный', 'SYSTEM', 96, 22, 0.6, 0, 'FISH'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Тунец консервированный' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Хек', 'хек', 'SYSTEM', 86, 16.6, 2.2, 0, 'FISH'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Хек' AND username = 'SYSTEM');

-- ============================================================
-- МОЛОЧНЫЕ ПРОДУКТЫ
-- ============================================================
INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Йогурт греческий', 'йогурт греческий', 'SYSTEM', 90, 9, 5, 4, 'DAIRY'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Йогурт греческий' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Сыр чеддер', 'сыр чеддер', 'SYSTEM', 403, 25, 33, 1.3, 'DAIRY'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Сыр чеддер' AND username = 'SYSTEM');

-- ============================================================
-- ОВОЩИ
-- ============================================================
INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Авокадо', 'авокадо', 'SYSTEM', 160, 2, 15, 9, 'VEGETABLES'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Авокадо' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Пекинская капуста', 'пекинская капуста', 'SYSTEM', 16, 1.2, 0.2, 2, 'VEGETABLES'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Пекинская капуста' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Салат Айсберг', 'салат айсберг', 'SYSTEM', 14, 0.9, 0.1, 2.9, 'VEGETABLES'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Салат Айсберг' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Цукини', 'цукини', 'SYSTEM', 17, 1.2, 0.3, 3.1, 'VEGETABLES'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Цукини' AND username = 'SYSTEM');

-- ============================================================
-- ФРУКТЫ И СУХОФРУКТЫ
-- ============================================================
INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Курага', 'курага', 'SYSTEM', 215, 5.2, 0.3, 51, 'FRUITS'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Курага' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Манго', 'манго', 'SYSTEM', 60, 0.8, 0.4, 15, 'FRUITS'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Манго' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Чернослив', 'чернослив', 'SYSTEM', 240, 2.3, 0.7, 57.5, 'FRUITS'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Чернослив' AND username = 'SYSTEM');

-- ============================================================
-- КРУПЫ, МУКА, ХЛЕБ
-- ============================================================
INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Лапша гречневая (сухая)', 'лапша гречневая (сухая)', 'SYSTEM', 350, 14, 0.7, 70, 'GRAINS'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Лапша гречневая (сухая)' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Мука рисовая', 'мука рисовая', 'SYSTEM', 366, 6, 1.4, 80, 'GRAINS'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Мука рисовая' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Ржаной хлебец', 'ржаной хлебец', 'SYSTEM', 310, 10, 4, 60, 'GRAINS'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Ржаной хлебец' AND username = 'SYSTEM');

-- ============================================================
-- ОРЕХИ, СЕМЕНА
-- ============================================================
INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Семена чиа', 'семена чиа', 'SYSTEM', 486, 16.5, 30.7, 42.1, 'OTHER'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Семена чиа' AND username = 'SYSTEM');

-- ============================================================
-- ПРОЧЕЕ
-- ============================================================
INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Псилиум', 'псилиум', 'SYSTEM', 200, 2, 0.5, 80, 'OTHER'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Псилиум' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Кофе', 'кофе', 'SYSTEM', 2, 0.1, 0, 0, 'DRINKS'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Кофе' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Лимонный сок', 'лимонный сок', 'SYSTEM', 22, 0.4, 0.1, 6.9, 'OTHER'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Лимонный сок' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Яичный белок пастеризованный', 'яичный белок пастеризованный', 'SYSTEM', 52, 11, 0.2, 0.7, 'OTHER'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Яичный белок пастеризованный' AND username = 'SYSTEM');

-- ============================================================
-- СЛАДКОЕ / БАТОНЧИКИ
-- ============================================================
INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Bombbar protein bar', 'bombbar protein bar', 'SYSTEM', 288, 33, 7.2, 5.8, 'SWEETS'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Bombbar protein bar' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Bombbar brownie cake', 'bombbar brownie cake', 'SYSTEM', 255, 15, 15, 6, 'SWEETS'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Bombbar brownie cake' AND username = 'SYSTEM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category)
SELECT 'Brownie банан+коллаген', 'brownie банан+коллаген', 'SYSTEM', 370, 12, 25, 18, 'SWEETS'
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Brownie банан+коллаген' AND username = 'SYSTEM');

-- ============================================================
-- ROLLBACK
-- ============================================================
-- rollback DELETE FROM ingredients WHERE username = 'SYSTEM' AND name IN (
--   'Индейка (филе бедра)', 'Сардина консервированная', 'Сёмга', 'Тунец консервированный', 'Хек',
--   'Йогурт греческий', 'Сыр чеддер',
--   'Авокадо', 'Пекинская капуста', 'Салат Айсберг', 'Цукини',
--   'Курага', 'Манго', 'Чернослив',
--   'Лапша гречневая (сухая)', 'Мука рисовая', 'Ржаной хлебец',
--   'Семена чиа', 'Псилиум', 'Кофе', 'Лимонный сок', 'Яичный белок пастеризованный',
--   'Bombbar protein bar', 'Bombbar brownie cake', 'Brownie банан+коллаген'
-- );