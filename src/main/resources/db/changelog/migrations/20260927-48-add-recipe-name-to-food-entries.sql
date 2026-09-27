-- liquibase formatted sql
-- changeset dmitriy:20260927-48-add-recipe-name-to-food-entries

-- ============================================================
-- ФИКС: сохранение recipe_name в food_entries
-- ============================================================
-- Проблема: при удалении рецепта дневник терял имя рецепта
-- Решение:
--   1. Добавить колонку recipe_name
--   2. Заполнить её из recipes для существующих записей
--   3. Изменить FK на ON DELETE SET NULL
-- ============================================================

-- 1. Добавить колонку (idempotent)
ALTER TABLE food_entries
ADD COLUMN IF NOT EXISTS recipe_name VARCHAR(255);

-- 2. Заполнить существующие записи из recipes
UPDATE food_entries fe
SET recipe_name = r.name
FROM recipes r
WHERE fe.recipe_id = r.id
  AND fe.recipe_name IS NULL;

-- 3. Удалить старый FK (если есть)
ALTER TABLE food_entries
DROP CONSTRAINT IF EXISTS fk_food_entries_recipe;

-- 4. Создать новый FK с ON DELETE SET NULL
ALTER TABLE food_entries
ADD CONSTRAINT fk_food_entries_recipe
FOREIGN KEY (recipe_id)
REFERENCES recipes(id)
ON DELETE SET NULL;

-- ============================================================
-- ROLLBACK
-- ============================================================
-- rollback ALTER TABLE food_entries DROP CONSTRAINT IF EXISTS fk_food_entries_recipe;
-- rollback ALTER TABLE food_entries ADD CONSTRAINT fk_food_entries_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE NO ACTION;
-- rollback ALTER TABLE food_entries DROP COLUMN IF EXISTS recipe_name;