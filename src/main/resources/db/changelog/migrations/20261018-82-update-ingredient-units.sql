--liquibase formatted sql

--changeset dmitriy:20261018-82-update-ingredient-units
-- Обновляем единицы измерения для популярных продуктов (яйца уже сделаны отдельно)

-- 1. Горчица (столовая ложка ~ 15 г)
UPDATE ingredients
SET unit_type = 'TBSP', grams_per_tbsp = 15
WHERE name_lower LIKE '%горчица%' AND username = 'SYSTEM';

-- 2. Майонез (столовая ложка ~ 20 г)
UPDATE ingredients
SET unit_type = 'TBSP', grams_per_tbsp = 20
WHERE name_lower LIKE '%майонез%' AND username = 'SYSTEM';

-- 3. Лимонный сок (столовая ложка ~ 15 г, чайная ~ 5 г)
UPDATE ingredients
SET unit_type = 'TBSP', grams_per_tbsp = 15, grams_per_tsp = 5
WHERE name_lower LIKE '%лимонный сок%' AND username = 'SYSTEM';

-- 4. Палочка корицы (1 палочка ~ 2 г)
UPDATE ingredients
SET unit_type = 'PIECE', unit_weight_grams = 2
WHERE name_lower LIKE '%корица%' AND name_lower LIKE '%палочка%' AND username = 'SYSTEM';

-- 5. Сахар (столовая ложка ~ 25 г, чайная ~ 8 г)
UPDATE ingredients
SET unit_type = 'TBSP', grams_per_tbsp = 25, grams_per_tsp = 8
WHERE name_lower LIKE '%сахар%' AND username = 'SYSTEM';

-- 6. Соль (чайная ложка ~ 10 г)
UPDATE ingredients
SET unit_type = 'TSP', grams_per_tsp = 10
WHERE name_lower LIKE '%соль%' AND username = 'SYSTEM';

-- 7. Масло растительное (столовая ложка ~ 17 г)
UPDATE ingredients
SET unit_type = 'TBSP', grams_per_tbsp = 17
WHERE name_lower LIKE '%масло растительное%' AND username = 'SYSTEM';

-- 8. Мука (ст.л. ~ 20 г, ч.л. ~ 7 г)
UPDATE ingredients
SET unit_type = 'TBSP', grams_per_tbsp = 20, grams_per_tsp = 7
WHERE name_lower LIKE '%мука%' AND username = 'SYSTEM';

-- 9. Соевый соус (ст.л. ~ 15 г)
UPDATE ingredients
SET unit_type = 'TBSP', grams_per_tbsp = 15
WHERE name_lower LIKE '%соевый соус%' AND username = 'SYSTEM';

-- 10. Сметана (ст.л. ~ 25 г)
UPDATE ingredients
SET unit_type = 'TBSP', grams_per_tbsp = 25
WHERE name_lower LIKE '%сметана%' AND username = 'SYSTEM';

-- 11. Йогурт (ст.л. ~ 20 г)
UPDATE ingredients
SET unit_type = 'TBSP', grams_per_tbsp = 20
WHERE name_lower LIKE '%йогурт%' AND username = 'SYSTEM';

-- ============================================================
-- ROLLBACK
-- ============================================================
-- rollback UPDATE ingredients SET unit_type = 'GRAM', grams_per_tbsp = NULL WHERE username = 'SYSTEM' AND name_lower LIKE '%горчица%';
-- rollback UPDATE ingredients SET unit_type = 'GRAM', grams_per_tbsp = NULL WHERE username = 'SYSTEM' AND name_lower LIKE '%майонез%';
-- rollback UPDATE ingredients SET unit_type = 'GRAM', grams_per_tbsp = NULL, grams_per_tsp = NULL WHERE username = 'SYSTEM' AND name_lower LIKE '%лимонный сок%';
-- rollback UPDATE ingredients SET unit_type = 'GRAM', unit_weight_grams = NULL WHERE username = 'SYSTEM' AND name_lower LIKE '%палочка%';
-- rollback UPDATE ingredients SET unit_type = 'GRAM', grams_per_tbsp = NULL, grams_per_tsp = NULL WHERE username = 'SYSTEM' AND name_lower LIKE '%сахар%';
-- rollback UPDATE ingredients SET unit_type = 'GRAM', grams_per_tsp = NULL WHERE username = 'SYSTEM' AND name_lower LIKE '%соль%';
-- rollback UPDATE ingredients SET unit_type = 'GRAM', grams_per_tbsp = NULL WHERE username = 'SYSTEM' AND name_lower LIKE '%масло растительное%';
-- rollback UPDATE ingredients SET unit_type = 'GRAM', grams_per_tbsp = NULL, grams_per_tsp = NULL WHERE username = 'SYSTEM' AND name_lower LIKE '%мука%';
-- rollback UPDATE ingredients SET unit_type = 'GRAM', grams_per_tbsp = NULL WHERE username = 'SYSTEM' AND name_lower LIKE '%соевый соус%';
-- rollback UPDATE ingredients SET unit_type = 'GRAM', grams_per_tbsp = NULL WHERE username = 'SYSTEM' AND name_lower LIKE '%сметана%';
-- rollback UPDATE ingredients SET unit_type = 'GRAM', grams_per_tbsp = NULL WHERE username = 'SYSTEM' AND name_lower LIKE '%йогурт%';