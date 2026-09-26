-- liquibase formatted sql
-- changeset dmitriy:20260926-43-create-body-measurements

CREATE TABLE body_measurements (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    measured_at  DATE         NOT NULL,
    weight_kg    DECIMAL(5,2),
    chest_cm     DECIMAL(5,2),
    waist_cm     DECIMAL(5,2),
    belly_cm     DECIMAL(5,2),
    hips_cm      DECIMAL(5,2),
    thigh_cm     DECIMAL(5,2),
    arm_cm       DECIMAL(5,2),
    neck_cm      DECIMAL(5,2),
    note         VARCHAR(500),
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_body_measurements_user_date
    ON body_measurements(user_id, measured_at DESC);

-- rollback DROP INDEX IF EXISTS idx_body_measurements_user_date;
-- rollback DROP TABLE IF EXISTS body_measurements;