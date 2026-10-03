--liquibase formatted sql

--changeset dmitriy:20261003-58-fix-is-checked-null
UPDATE shopping_list_items SET is_checked = false WHERE is_checked IS NULL;
--rollback -- no-op

--changeset dmitriy:20261003-58-set-default-not-null
ALTER TABLE shopping_list_items ALTER COLUMN is_checked SET DEFAULT false;
ALTER TABLE shopping_list_items ALTER COLUMN is_checked SET NOT NULL;
--rollback ALTER TABLE shopping_list_items ALTER COLUMN is_checked DROP NOT NULL;
--rollback ALTER TABLE shopping_list_items ALTER COLUMN is_checked DROP DEFAULT;