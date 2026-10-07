--liquibase formatted sql

--changeset dmitriy:20261006-63-add-ingredients-name-lower
ALTER TABLE ingredients
    ADD COLUMN name_lower VARCHAR(255);
--rollback ALTER TABLE ingredients DROP COLUMN name_lower;

--changeset dmitriy:20261006-64-backfill-ingredients-name-lower
UPDATE ingredients
SET name_lower = translate(
    translate(name,
        'АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ',
        'абвгдеёжзийклмнопрстуфхцчшщъыьэюя'),
    'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
    'abcdefghijklmnopqrstuvwxyz'
)
WHERE name_lower IS NULL;
--rollback UPDATE ingredients SET name_lower = NULL WHERE name_lower IS NOT NULL;

--changeset dmitriy:20261006-65-ingredients-name-lower-not-null
ALTER TABLE ingredients
    ALTER COLUMN name_lower SET NOT NULL;
--rollback ALTER TABLE ingredients ALTER COLUMN name_lower DROP NOT NULL;