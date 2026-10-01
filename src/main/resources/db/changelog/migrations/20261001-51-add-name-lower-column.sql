--liquibase formatted sql

--changeset dmitriy:20261001-51-add-name-lower-column
ALTER TABLE recipes ADD COLUMN IF NOT EXISTS name_lower TEXT;
--rollback ALTER TABLE recipes DROP COLUMN IF EXISTS name_lower;

--changeset dmitriy:2026-10-01-backfill-name-lower
UPDATE recipes
SET name_lower = translate(
    translate(name,
        'АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ',
        'абвгдеёжзийклмнопрстуфхцчшщъыьэюя'),
    'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
    'abcdefghijklmnopqrstuvwxyz'
);
--rollback UPDATE recipes SET name_lower = NULL;