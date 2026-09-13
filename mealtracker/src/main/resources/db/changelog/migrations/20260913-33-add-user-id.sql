-- liquibase formatted sql
-- changeset dmitriy:33-add-user-id
ALTER TABLE recipes ADD COLUMN user_id BIGINT;

ALTER TABLE recipes
ADD CONSTRAINT fk_recipes_user
FOREIGN KEY (user_id) REFERENCES users(id);