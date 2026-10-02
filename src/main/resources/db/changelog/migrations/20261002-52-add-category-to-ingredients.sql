--liquibase formatted sql

--changeset dmitriy:20261002-52-add-category-column
ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS category VARCHAR(50) DEFAULT 'OTHER';
--rollback ALTER TABLE ingredients DROP COLUMN IF EXISTS category;

--changeset dmitriy:20261002-52-backfill-categories
UPDATE ingredients SET category = 'OTHER' WHERE category IS NULL;
--rollback UPDATE ingredients SET category = NULL WHERE category = 'OTHER';