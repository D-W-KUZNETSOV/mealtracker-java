--liquibase formatted sql

--changeset dmitriy:2026-10-04-61-1-add-reset-token
ALTER TABLE users ADD COLUMN IF NOT EXISTS reset_token VARCHAR(255);
--rollback ALTER TABLE users DROP COLUMN IF EXISTS reset_token;

--changeset dmitriy:2026-10-04-61-2-add-reset-token-expires
ALTER TABLE users ADD COLUMN IF NOT EXISTS reset_token_expires_at TIMESTAMP;
--rollback ALTER TABLE users DROP COLUMN IF EXISTS reset_token_expires_at;