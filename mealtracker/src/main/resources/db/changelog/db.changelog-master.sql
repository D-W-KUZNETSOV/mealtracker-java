
--liquibase formatted sql

--changeset you:1
ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS fats_per100g DOUBLE PRECISION;
ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS proteins_per100g DOUBLE PRECISION;
ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS carbs_per100g DOUBLE PRECISION;

--changeset you:2
ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS test_column VARCHAR(255);