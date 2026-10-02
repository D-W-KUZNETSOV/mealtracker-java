--liquibase formatted sql

--changeset dmitriy:20261002-53-create-meal-plans
CREATE TABLE IF NOT EXISTS meal_plans (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_meal_plans_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);
--rollback DROP TABLE IF EXISTS meal_plans;
