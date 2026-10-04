--liquibase formatted sql

--changeset dmitriy:2026-10-04-60-1-add-deleted-at
ALTER TABLE users ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
--rollback ALTER TABLE users DROP COLUMN IF EXISTS deleted_at;

--changeset dmitriy:2026-10-04-60-2-fix-recipes-fk
ALTER TABLE recipes DROP CONSTRAINT IF EXISTS fk_recipes_user;
ALTER TABLE recipes ADD CONSTRAINT fk_recipes_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;
--rollback ALTER TABLE recipes DROP CONSTRAINT IF EXISTS fk_recipes_user;
--rollback ALTER TABLE recipes ADD CONSTRAINT fk_recipes_user
--rollback     FOREIGN KEY (user_id) REFERENCES users(id);

--changeset dmitriy:2026-10-04-60-3-fix-goals-fk
ALTER TABLE user_goals DROP CONSTRAINT IF EXISTS fk_user_goals_user;
ALTER TABLE user_goals ADD CONSTRAINT fk_user_goals_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
--rollback ALTER TABLE user_goals DROP CONSTRAINT IF EXISTS fk_user_goals_user;
--rollback ALTER TABLE user_goals ADD CONSTRAINT fk_user_goals_user
--rollback     FOREIGN KEY (user_id) REFERENCES users(id);

--changeset dmitriy:2026-10-04-60-4-fix-food-entries-fk
ALTER TABLE food_entries DROP CONSTRAINT IF EXISTS fk_food_entries_daily_log;
ALTER TABLE food_entries ADD CONSTRAINT fk_food_entries_daily_log
    FOREIGN KEY (daily_log_id) REFERENCES daily_logs(id) ON DELETE CASCADE;
--rollback ALTER TABLE food_entries DROP CONSTRAINT IF EXISTS fk_food_entries_daily_log;
--rollback ALTER TABLE food_entries ADD CONSTRAINT fk_food_entries_daily_log
--rollback     FOREIGN KEY (daily_log_id) REFERENCES daily_logs(id);