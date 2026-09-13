-- liquibase formatted sql
-- changeset dmitriy:30-add-fk-user-goals-user
ALTER TABLE user_goals
    ADD CONSTRAINT fk_user_goals_user
    FOREIGN KEY (user_id) REFERENCES users(id);