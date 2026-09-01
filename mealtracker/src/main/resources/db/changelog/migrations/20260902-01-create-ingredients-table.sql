--liquibase formatted sql
--changeset dmitriy:20260902-01-create-ingredients-table-v1 runInTransaction:false
CREATE TABLE ingredients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    fats_per100g DOUBLE PRECISION,
    proteins_per100g DOUBLE PRECISION,
    carbs_per100g DOUBLE PRECISION,
    calories_per100g DOUBLE PRECISION
);

