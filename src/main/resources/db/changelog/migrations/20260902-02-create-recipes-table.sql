--liquibase formatted sql
--changeset dmitriy:20260902-02-create-recipes-table-v1 validCheckSum:any runInTransaction:false

CREATE TABLE IF NOT EXISTS recipes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);


