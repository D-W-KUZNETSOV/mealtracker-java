--liquibase formatted sql

--changeset dmitriy:20261010-75-fix-other-categories
-- Финальная чистка OTHER (7 записей)

-- Кукуруза, Фасоль, Оливки → VEGETABLES
UPDATE ingredients SET category = 'VEGETABLES'
WHERE username = 'SYSTEM' AND name IN (
  'Кукуруза консервированная', 'Фасоль красная', 'Оливки'
);

-- Лимонный сок → DRESSINGS
UPDATE ingredients SET category = 'DRESSINGS'
WHERE username = 'SYSTEM' AND name = 'Лимонный сок';

-- Майонез → DRESSINGS (был в OILS)
UPDATE ingredients SET category = 'DRESSINGS'
WHERE username = 'SYSTEM' AND name = 'Майонез';

-- Семена чиа → NUTS
UPDATE ingredients SET category = 'NUTS'
WHERE username = 'SYSTEM' AND name = 'Семена чиа';

-- Яичный белок → EGGS
UPDATE ingredients SET category = 'EGGS'
WHERE username = 'SYSTEM' AND name = 'Яичный белок пастеризованный';

-- Псилиум остаётся в OTHER