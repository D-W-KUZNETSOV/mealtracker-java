-- liquibase formatted sql
-- changeset dmitriy:20260926-41-add-goal-type-and-overrides

-- Тип цели (по умолчанию — поддержание)
ALTER TABLE user_goals
    ADD COLUMN IF NOT EXISTS goal_type VARCHAR(20) NOT NULL DEFAULT 'MAINTAIN';

-- Ручная корректировка белка (nullable — если null, используется авто)
ALTER TABLE user_goals
    ADD COLUMN IF NOT EXISTS target_protein_override DECIMAL(6,2);

-- Ручная корректировка калорий (nullable — если null, используется авто)
ALTER TABLE user_goals
    ADD COLUMN IF NOT EXISTS target_calories_override INTEGER;

-- rollback ALTER TABLE user_goals DROP COLUMN IF EXISTS goal_type;
-- rollback ALTER TABLE user_goals DROP COLUMN IF EXISTS target_protein_override;
-- rollback ALTER TABLE user_goals DROP COLUMN IF EXISTS target_calories_override;