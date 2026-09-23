-- liquibase formatted sql
-- changeset dmitriy:20260906-add-email-to-users.sql
ALTER TABLE users ADD COLUMN email VARCHAR(255);

-- Опционально: сделать email уникальным (чтобы не было дублей)
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users(email);
