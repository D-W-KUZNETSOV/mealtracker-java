--liquibase formatted sql

--changeset dmitriy:20261018-80-remove-default-unit
-- Удаляем колонку default_unit, так как используем unit_type (enum)

ALTER TABLE ingredients DROP COLUMN default_unit;

-- rollback ALTER TABLE ingredients ADD COLUMN default_unit VARCHAR(20);
