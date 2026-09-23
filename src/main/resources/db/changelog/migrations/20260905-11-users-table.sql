-- liquibase formatted sql
-- changeset dmitriy:20260905-11-users-table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER' NOT NULL
);

COMMENT ON TABLE users IS 'Пользователи приложения Mealtracker';
COMMENT ON COLUMN users.username IS 'Логин для входа (уникальный)';
COMMENT ON COLUMN users.password IS 'Пароль в виде BCrypt-хеша';
COMMENT ON COLUMN users.role IS 'Роль пользователя: USER или ADMIN';
