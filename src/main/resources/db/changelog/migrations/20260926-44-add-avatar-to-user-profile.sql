-- liquibase formatted sql
-- changeset dmitriy:20260926-44-add-avatar-to-user-profile

ALTER TABLE user_profile
    ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(512);

-- rollback ALTER TABLE user_profile DROP COLUMN IF EXISTS avatar_url;