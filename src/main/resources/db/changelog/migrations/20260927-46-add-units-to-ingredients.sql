-- liquibase formatted sql
-- changeset dmitriy:20260927-46-add-units-to-ingredients

-- Тип единицы измерения (PIECE, ML, TBSP, TSP, CUP, GRAM)
ALTER TABLE ingredients
    ADD COLUMN IF NOT EXISTS unit_type VARCHAR(20) DEFAULT 'GRAM';

-- Вес в граммах для 1 единицы (например, 1 яйцо = 55 г)
ALTER TABLE ingredients
    ADD COLUMN IF NOT EXISTS unit_weight_grams DECIMAL(8,2);

-- ============================================================
-- Заполняем для базовых ингредиентов
-- ============================================================

-- Яйца: 1 шт = 55 г
UPDATE ingredients SET unit_type = 'PIECE', unit_weight_grams = 55.00
WHERE name IN ('Яйцо куриное', 'Яйцо куриное (сырое)') AND username = 'SYSTEM';

-- Молоко: 1 мл = 1.03 г
UPDATE ingredients SET unit_type = 'ML', unit_weight_grams = 1.03
WHERE name LIKE 'Молоко%' AND username = 'SYSTEM';

-- Масло растительное: 1 ст.л. = 15 г
UPDATE ingredients SET unit_type = 'TBSP', unit_weight_grams = 15.00
WHERE name IN ('Масло растительное', 'Масло оливковое') AND username = 'SYSTEM';

-- Масло сливочное: 1 ст.л. = 20 г
UPDATE ingredients SET unit_type = 'TBSP', unit_weight_grams = 20.00
WHERE name = 'Масло сливочное' AND username = 'SYSTEM';

-- Мёд: 1 ст.л. = 21 г
UPDATE ingredients SET unit_type = 'TBSP', unit_weight_grams = 21.00
WHERE name = 'Мёд' AND username = 'SYSTEM';

-- Сахар: 1 ст.л. = 25 г, 1 ч.л. = 8 г
UPDATE ingredients SET unit_type = 'TBSP', unit_weight_grams = 25.00
WHERE name IN ('Сахар', 'Сахар белый') AND username = 'SYSTEM';

-- Соль: 1 ч.л. = 10 г
UPDATE ingredients SET unit_type = 'TSP', unit_weight_grams = 10.00
WHERE name = 'Соль' AND username = 'SYSTEM';

-- rollback ALTER TABLE ingredients DROP COLUMN IF EXISTS unit_type;
-- rollback ALTER TABLE ingredients DROP COLUMN IF EXISTS unit_weight_grams;