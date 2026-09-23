-- liquibase formatted sql
-- changeset dmitriy:31-fill-user-id-from-username
UPDATE user_goals ug
SET user_id = (SELECT u.id FROM users u WHERE u.username = ug.username)
WHERE user_id IS NULL;