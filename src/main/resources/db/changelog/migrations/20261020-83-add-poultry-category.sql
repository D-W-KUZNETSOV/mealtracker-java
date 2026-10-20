--liquibase formatted sql

--changeset dmitriy:20261020-83-add-poultry-category
-- Переносим птицу из MEAT в POULTRY

UPDATE ingredients
SET category = 'POULTRY'
WHERE username = 'SYSTEM'
  AND (name_lower LIKE '%куриц%'
    OR name_lower LIKE '%курин%'
    OR name_lower LIKE '%индейк%'
    OR name_lower LIKE '%утк%'
    OR name_lower LIKE '%утен%'
    OR name_lower LIKE '%гусь%'
    OR name_lower LIKE '%гусин%');

-- ============================================================
-- ROLLBACK
-- ============================================================
-- rollback UPDATE ingredients SET category = 'MEAT' WHERE username = 'SYSTEM' AND category = 'POULTRY';