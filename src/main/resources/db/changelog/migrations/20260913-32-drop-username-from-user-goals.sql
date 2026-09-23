-- liquibase formatted sql
-- changeset dmitriy:30-drop-username-from-user-goals
ALTER TABLE user_goals DROP COLUMN username;