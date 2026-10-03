--liquibase formatted sql

--changeset dmitriy:20261003-59-add-steps-column
ALTER TABLE recipes ADD COLUMN IF NOT EXISTS steps TEXT;
--rollback ALTER TABLE recipes DROP COLUMN IF EXISTS steps;