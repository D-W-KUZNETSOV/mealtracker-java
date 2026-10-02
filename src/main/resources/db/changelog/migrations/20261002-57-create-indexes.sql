--liquibase formatted sql
--changeset dmitriy:20261002-57-create-indexes
CREATE INDEX IF NOT EXISTS idx_meal_plans_user ON meal_plans(user_id);
CREATE INDEX IF NOT EXISTS idx_meal_plan_items_plan ON meal_plan_items(plan_id);
CREATE INDEX IF NOT EXISTS idx_meal_plan_items_date ON meal_plan_items(plan_date);
CREATE INDEX IF NOT EXISTS idx_shopping_lists_user ON shopping_lists(user_id);
CREATE INDEX IF NOT EXISTS idx_shopping_lists_status ON shopping_lists(user_id, status);
CREATE INDEX IF NOT EXISTS idx_shopping_list_items_list ON shopping_list_items(list_id);
--rollback DROP INDEX IF EXISTS idx_meal_plans_user;
--rollback DROP INDEX IF EXISTS idx_meal_plan_items_plan;
--rollback DROP INDEX IF EXISTS idx_meal_plan_items_date;
--rollback DROP INDEX IF EXISTS idx_shopping_lists_user;
--rollback DROP INDEX IF EXISTS idx_shopping_lists_status;
--rollback DROP INDEX IF EXISTS idx_shopping_list_items_list;