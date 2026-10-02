--liquibase formatted sql
--changeset dmitriy:20261002-55-create-shopping-lists
CREATE TABLE IF NOT EXISTS shopping_lists (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    plan_id BIGINT,
    name VARCHAR(255),
    period_start DATE,
    period_end DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_shopping_lists_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_shopping_lists_plan FOREIGN KEY (plan_id)
        REFERENCES meal_plans(id) ON DELETE SET NULL
);
--rollback DROP TABLE IF EXISTS shopping_lists;