--liquibase formatted sql

--changeset dmitriy:20261018-79-add-ingredient-units
-- Добавление единиц измерения (штуки, ложки) для ингредиентов

ALTER TABLE ingredients ADD COLUMN default_unit VARCHAR(20) DEFAULT 'g';
ALTER TABLE ingredients ADD COLUMN grams_per_piece DECIMAL(10, 2);
ALTER TABLE ingredients ADD COLUMN grams_per_tbsp DECIMAL(10, 2);
ALTER TABLE ingredients ADD COLUMN grams_per_tsp DECIMAL(10, 2);

-- ============================================================
-- ROLLBACK
-- ============================================================
-- rollback ALTER TABLE ingredients DROP COLUMN default_unit;
-- rollback ALTER TABLE ingredients DROP COLUMN grams_per_piece;
-- rollback ALTER TABLE ingredients DROP COLUMN grams_per_tbsp;
-- rollback ALTER TABLE ingredients DROP COLUMN grams_per_tsp;