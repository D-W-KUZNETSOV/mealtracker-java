-- liquibase formatted sql
-- changeset dmitriy:20260926-42-drop-activity-level-from-goals

ALTER TABLE user_goals DROP COLUMN IF EXISTS activity_level;

-- rollback ALTER TABLE user_goals ADD COLUMN activity_level VARCHAR(20) NOT NULL DEFAULT 'SEDENTARY';