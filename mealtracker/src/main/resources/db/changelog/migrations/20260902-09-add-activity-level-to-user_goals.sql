-- liquibase formatted sql
-- changeset dmitriy:20260902-09-add-activity-level-to-user_goals runInTransaction:false
ALTER TABLE user_goals
ADD COLUMN activity_level VARCHAR(255) NOT NULL DEFAULT 'SEDENTARY';
