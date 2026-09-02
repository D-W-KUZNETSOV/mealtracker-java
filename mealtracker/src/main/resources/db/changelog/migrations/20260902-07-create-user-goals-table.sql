--liquibase formatted sql
--changeset dmitriy:20260902-07-create-user-goals-table runInTransaction:false
CREATE TABLE user_goals (
    id BIGSERIAL PRIMARY KEY,
    current_weight_kg DOUBLE PRECISION NOT NULL,
    protein_per_kg DOUBLE PRECISION NOT NULL,
    target_calories INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


