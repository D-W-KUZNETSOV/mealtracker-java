-- liquibase formatted sql
-- changeset dmitriy:add-current-weight-column
ALTER TABLE user_profile
    ADD COLUMN current_weight_kg DECIMAL(5,2);
