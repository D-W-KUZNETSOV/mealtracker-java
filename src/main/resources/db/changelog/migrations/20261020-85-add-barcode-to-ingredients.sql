--liquibase formatted sql

--changeset dmitriy:20261020-85-add-barcode-to-ingredients
ALTER TABLE ingredients ADD COLUMN barcode VARCHAR(20);
CREATE INDEX idx_ingredients_barcode_username ON ingredients (barcode, username);

-- rollback DROP INDEX idx_ingredients_barcode_username;
-- rollback ALTER TABLE ingredients DROP COLUMN barcode;