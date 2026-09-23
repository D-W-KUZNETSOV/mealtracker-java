-- liquibase formatted sql
-- changeset dmitriy:28-make_activity_level_not_null
ALTER TABLE user_profile
    ALTER COLUMN activity_level SET NOT NULL;
