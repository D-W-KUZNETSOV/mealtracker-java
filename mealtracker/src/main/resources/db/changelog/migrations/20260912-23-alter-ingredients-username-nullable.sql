--liquibase formatted sql

--changeset you:alter-ingredients-username-nullable
ALTER TABLE ingredients ALTER COLUMN username DROP NOT NULL;
