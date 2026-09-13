--liquibase formatted sql

--changeset dmitriy:38-create-food-entries
CREATE TABLE food_entries (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    daily_log_id      BIGINT       NOT NULL,
    recipe_id         BIGINT,
    product_name      VARCHAR(255),
    weight_in_grams   DOUBLE PRECISION NOT NULL,
    calories          NUMERIC(10,2) NOT NULL DEFAULT 0,
    calories_per100g  NUMERIC(10,2) NOT NULL DEFAULT 0,
    proteins          NUMERIC(10,2) NOT NULL DEFAULT 0,
    protein_per100g   NUMERIC(10,2) NOT NULL DEFAULT 0,
    fats              NUMERIC(10,2) NOT NULL DEFAULT 0,
    fat_per100g       NUMERIC(10,2) NOT NULL DEFAULT 0,
    carbs             NUMERIC(10,2) NOT NULL DEFAULT 0,
    carbs_per100g     NUMERIC(10,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_food_entries_daily_log
        FOREIGN KEY (daily_log_id) REFERENCES daily_logs (id),
    CONSTRAINT fk_food_entries_recipe
        FOREIGN KEY (recipe_id) REFERENCES recipes (id)
);