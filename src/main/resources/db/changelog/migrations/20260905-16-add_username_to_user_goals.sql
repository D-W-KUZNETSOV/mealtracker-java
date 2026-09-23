-- liquibase formatted sql
-- changeset dmitriy:20260905-16-add_username_to_user_goals

-- Добавляем колонку username
ALTER TABLE user_goals ADD COLUMN username VARCHAR(255);

-- Заполняем существующие записи значением по умолчанию (подставь своего юзера)
UPDATE user_goals SET username = 'dmitriy' WHERE username IS NULL;

-- Делаем колонку NOT NULL
ALTER TABLE user_goals ALTER COLUMN username SET NOT NULL;

-- Создаём индекс для быстрого поиска по пользователю
CREATE INDEX IF NOT EXISTS idx_user_goals_username ON user_goals (username);
