-- liquibase formatted sql
-- changeset dmitriy:29-add-user-id-to-user-goals
ALTER TABLE user_goals ADD COLUMN user_id BIGINT;