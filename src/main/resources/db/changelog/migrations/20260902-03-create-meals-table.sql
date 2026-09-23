--liquibase formatted sql
--changeset dmitriy:20260902-03-create-meals-table-v1 runInTransaction:false
CREATE TABLE meals (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    date DATE NOT NULL
);

