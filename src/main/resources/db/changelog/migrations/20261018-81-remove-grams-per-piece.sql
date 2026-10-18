--liquibase formatted sql

--changeset dmitriy:20261018-81-remove-grams-per-piece
ALTER TABLE ingredients DROP COLUMN grams_per_piece;

-- rollback ALTER TABLE ingredients ADD COLUMN grams_per_piece DECIMAL(10, 2);