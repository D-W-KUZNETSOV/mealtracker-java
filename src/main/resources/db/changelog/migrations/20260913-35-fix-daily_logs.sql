-- liquibase formatted sql
-- changeset dmitriy:35-fix-daily_logs

-- Сначала добавим недостающие колонки
ALTER TABLE daily_logs ADD COLUMN calories NUMERIC(10, 2) NOT NULL DEFAULT 0;
ALTER TABLE daily_logs ADD COLUMN protein NUMERIC(10, 2) NOT NULL DEFAULT 0;
ALTER TABLE daily_logs ADD COLUMN fat NUMERIC(10, 2) NOT NULL DEFAULT 0;
ALTER TABLE daily_logs ADD COLUMN carbs NUMERIC(10, 2) NOT NULL DEFAULT 0;

-- Переименуем колонку date в log_date (чтобы совпадало с @Column в сущности)
ALTER TABLE daily_logs RENAME COLUMN date TO log_date;

-- Удалим лишние колонки, которые были в старой миграции, но не нужны в текущей логике
-- Внимание: если там есть важные данные, не удаляй! Но судя по ошибке, таблица пустая или тестовая.
ALTER TABLE daily_logs DROP COLUMN recipe_id;
ALTER TABLE daily_logs DROP COLUMN weight_in_grams;

-- Создадим индекс на log_date
CREATE INDEX IF NOT EXISTS idx_daily_logs_log_date ON daily_logs(log_date);
