--liquibase formatted sql
--changeset dmitriy:20260902-01-create-ingredients-table-v1 validCheckSum:any runInTransaction:false
CREATE TABLE IF NOT EXISTS ingredients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    fats_per100g DOUBLE PRECISION,
    proteins_per100g DOUBLE PRECISION,
    carbs_per100g DOUBLE PRECISION,
    calories_per100g DOUBLE PRECISION
);


