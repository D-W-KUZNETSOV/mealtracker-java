-- liquibase formatted sql
-- changeset dmitriy:20260903-10-create-daily_logs runInTransaction:false
CREATE TABLE daily_logs (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    weight_in_grams DOUBLE PRECISION NOT NULL,
    date DATE NOT NULL
);

CREATE INDEX idx_daily_logs_date ON daily_logs(date);
CREATE INDEX idx_daily_logs_recipe ON daily_logs(recipe_id);
