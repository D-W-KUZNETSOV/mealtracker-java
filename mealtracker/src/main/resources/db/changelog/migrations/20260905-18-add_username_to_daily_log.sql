-- liquibase formatted sql
-- liquibase formatted sql
-- changeset dmitriy:20260905-18-add_username_to_daily_log

UPDATE daily_logs SET username = 'dmitriy' WHERE username IS NULL;

ALTER TABLE daily_logs ALTER COLUMN username SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_daily_logs_username ON daily_logs (username);
CREATE INDEX IF NOT EXISTS idx_daily_logs_date_username ON daily_logs (date, username);

-- rollback
-- DROP INDEX IF EXISTS idx_daily_logs_date_username;
-- DROP INDEX IF EXISTS idx_daily_logs_username;
