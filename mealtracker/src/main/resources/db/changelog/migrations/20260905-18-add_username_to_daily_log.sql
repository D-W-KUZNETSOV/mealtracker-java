-- liquibase formatted sql
-- changeset dmitriy:20260905-18-add_username_to_daily_log validCheckSum:any runOnChange:true

-- 1. Добавляем колонку (безопасно для обеих БД)
ALTER TABLE daily_logs ADD COLUMN IF NOT EXISTS username VARCHAR(255);

-- 2. Заполняем старые записи
UPDATE daily_logs SET username = 'dmitriy' WHERE username IS NULL;

-- !!! ВНИМАНИЕ: Эту строку можно закомментировать, если на PostgreSQL будет ошибка !!!
-- ALTER TABLE daily_logs ALTER COLUMN username SET NOT NULL;

-- 3. Индексы
CREATE INDEX IF NOT EXISTS idx_daily_logs_username ON daily_logs (username);
CREATE INDEX IF NOT EXISTS idx_daily_logs_date_username ON daily_logs (date, username);
