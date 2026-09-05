-- liquibase formatted sql
-- changeset dmitriy:20260906-19-base-ingredients

INSERT INTO ingredients (name, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Яйцо куриное (сырое)', 155, 12.7, 11.5, 0.7
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Яйцо куриное (сырое)');

INSERT INTO ingredients (name, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Куриная грудка (сырая)', 165, 31.0, 3.6, 0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Куриная грудка (сырая)');

INSERT INTO ingredients (name, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Рис (сухой)', 360, 7.5, 0.5, 80
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Рис (сухой)');

INSERT INTO ingredients (name, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Овсяные хлопья (сухие)', 379, 13.2, 6.5, 67.7
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Овсяные хлопья (сухие)');

INSERT INTO ingredients (name, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Молоко 3.2%', 64, 3.0, 3.2, 4.7
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Молоко 3.2%');

INSERT INTO ingredients (name, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Соль', 0, 0, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Соль');

INSERT INTO ingredients (name, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Сахар белый', 387, 0, 0, 99.9
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Сахар белый');

INSERT INTO ingredients (name, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Масло сливочное 82.5%', 748, 0.6, 82.5, 0.8
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Масло сливочное 82.5%');
