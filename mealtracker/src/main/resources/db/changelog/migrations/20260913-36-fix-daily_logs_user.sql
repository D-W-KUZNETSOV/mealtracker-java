-- liquibase formatted sql
-- changeset dmitriy:36-fix-daily_logs_user

-- Добавляем user_id
ALTER TABLE daily_logs ADD COLUMN user_id BIGINT;

-- Делаем user_id NOT NULL
ALTER TABLE daily_logs ALTER COLUMN user_id SET NOT NULL;

-- Внешний ключ
ALTER TABLE daily_logs ADD CONSTRAINT fk_daily_logs_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Сначала удаляем индекс, который ссылается на username
DROP INDEX IF EXISTS idx_daily_logs_date_username;

-- Теперь можно удалить колонку
ALTER TABLE daily_logs DROP COLUMN IF EXISTS username;

-- Новый индекс
CREATE INDEX IF NOT EXISTS idx_daily_logs_user_date ON daily_logs(user_id, log_date);

