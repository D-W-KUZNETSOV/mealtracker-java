--liquibase formatted sql

--changeset dmitriy:20261017-01-add-display-name
-- Добавление поля display_name (как обращаться к юзеру) в таблицу users

ALTER TABLE users ADD COLUMN display_name VARCHAR(255);

-- ============================================================
-- ROLLBACK
-- ============================================================
-- rollback ALTER TABLE users DROP COLUMN display_name;