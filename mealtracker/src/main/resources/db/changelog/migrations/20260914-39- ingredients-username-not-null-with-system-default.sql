-- liquibase formatted sql
-- changeset dmitriy:39-ingredients-username-not-null-with-system-default

-- Шаг 1: удалить дубликаты по (name), оставив одну строку.
-- Приоритет: сначала строки с username = 'unknown' (старые базовые),
-- потом NULL, потом всё остальное.
DELETE FROM ingredients
WHERE id NOT IN (
    SELECT MIN(id)
    FROM ingredients
    GROUP BY name
);

-- Шаг 2: теперь безопасно обновить username
UPDATE ingredients SET username = 'SYSTEM' WHERE username IS NULL OR username = 'unknown';
ALTER TABLE ingredients ALTER COLUMN username SET DEFAULT 'SYSTEM';
ALTER TABLE ingredients ALTER COLUMN username SET NOT NULL;

-- rollback ALTER TABLE ingredients ALTER COLUMN username DROP NOT NULL;
-- rollback ALTER TABLE ingredients ALTER COLUMN username DROP DEFAULT;
-- rollback UPDATE ingredients SET username = NULL WHERE username = 'SYSTEM';