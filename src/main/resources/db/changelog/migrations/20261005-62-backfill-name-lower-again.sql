--liquibase formatted sql

--changeset dmitriy:20261005-62-backfill-name-lower-null
UPDATE recipes
SET name_lower = translate(
    translate(name,
        'АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ',
        'абвгдеёжзийклмнопрстуфхцчшщъыьэюя'),
    'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
    'abcdefghijklmnopqrstuvwxyz'
)
WHERE name_lower IS NULL;
--rollback UPDATE recipes SET name_lower = NULL WHERE name_lower IS NOT NULL;