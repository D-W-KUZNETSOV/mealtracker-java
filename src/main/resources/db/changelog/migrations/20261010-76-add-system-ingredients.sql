--liquibase formatted sql

--changeset dmitriy:20261010-76-add-system-ingredients
-- Добавление 22 базовых ингредиентов в SYSTEM

-- Курица (7)
INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Куриные голени (сырые)', 'куриные голени (сырые)', 'SYSTEM', 158, 19, 8.5, 0, 'MEAT', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Куриные голени варёные', 'куриные голени варёные', 'SYSTEM', 170, 22, 8, 0, 'MEAT', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Куриные крылья (сырые)', 'куриные крылья (сырые)', 'SYSTEM', 185, 19.5, 12.5, 0, 'MEAT', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Куриные крылья запечённые', 'куриные крылья запечённые', 'SYSTEM', 240, 22, 17, 0, 'MEAT', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Куриная спинка (сырая)', 'куриная спинка (сырая)', 'SYSTEM', 200, 17, 15, 0, 'MEAT', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Куриные сердечки', 'куриные сердечки', 'SYSTEM', 153, 15.8, 10.6, 0.4, 'MEAT', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Куриные желудки', 'куриные желудки', 'SYSTEM', 130, 20, 3.9, 0, 'MEAT', 'GRAM');

-- Грибы (3)
INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Шампиньоны', 'шампиньоны', 'SYSTEM', 27, 4.3, 1, 0.1, 'VEGETABLES', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Белые грибы', 'белые грибы', 'SYSTEM', 34, 3.7, 1.7, 1.1, 'VEGETABLES', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Вешенки', 'вешенки', 'SYSTEM', 33, 3.3, 0.4, 6.1, 'VEGETABLES', 'GRAM');

-- Рыба (8)
INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Горбуша', 'горбуша', 'SYSTEM', 142, 21, 7, 0, 'FISH', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Кета', 'кета', 'SYSTEM', 138, 22, 5.6, 0, 'FISH', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Форель', 'форель', 'SYSTEM', 104, 20, 2, 0, 'FISH', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Дорадо', 'дорадо', 'SYSTEM', 96, 20, 2, 0, 'FISH', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Сибас', 'сибас', 'SYSTEM', 97, 18, 2, 0, 'FISH', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Тилапия', 'тилапия', 'SYSTEM', 96, 20, 1.7, 0, 'FISH', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Мидии', 'мидии', 'SYSTEM', 77, 12, 2, 3, 'FISH', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Осьминог', 'осьминог', 'SYSTEM', 73, 15, 1, 0, 'FISH', 'GRAM');

-- Прочее (4)
INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Лайм', 'лайм', 'SYSTEM', 30, 0.9, 0.2, 8.4, 'FRUITS', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Кунжут', 'кунжут', 'SYSTEM', 573, 17.7, 48.7, 23.5, 'NUTS', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Горчица', 'горчица', 'SYSTEM', 162, 5.7, 5.4, 22.6, 'DRESSINGS', 'GRAM');

INSERT INTO ingredients (name, name_lower, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g, category, unit_type)
VALUES ('Соевый соус', 'соевый соус', 'SYSTEM', 53, 8, 0, 8, 'DRESSINGS', 'GRAM');