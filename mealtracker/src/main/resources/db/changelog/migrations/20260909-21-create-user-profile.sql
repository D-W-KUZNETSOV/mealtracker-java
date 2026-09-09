-- liquibase formatted sql
-- changeset dmitriy:create-user-profile-table
CREATE TABLE user_profile (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    height_cm INTEGER,
    target_weight_kg DECIMAL(5,2),
    gender VARCHAR(10),
    activity_level VARCHAR(20) DEFAULT 'MODERATE',
    date_of_birth DATE,
    CONSTRAINT fk_user_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
