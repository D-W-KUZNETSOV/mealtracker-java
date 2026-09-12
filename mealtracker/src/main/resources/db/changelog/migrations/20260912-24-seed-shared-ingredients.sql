--liquibase formatted sql

--changeset you:seed-shared-ingredients
INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
VALUES ('Куриная грудка', NULL, 140, 31.0, 1.5, 0.0);

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
VALUES ('Гречка варёная', NULL, 132, 4.2, 1.5, 28.7);

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
VALUES ('Яйцо куриное', NULL, 157, 12.7, 11.5, 0.7);

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
VALUES ('Творог 5%', NULL, 121, 17.0, 5.0, 3.0);

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
VALUES ('Огурец свежий', NULL, 15, 0.8, 0.1, 2.8);

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
VALUES ('Рис варёный', NULL, 116, 2.7, 0.3, 28.0);

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
VALUES ('Овсянка на воде', NULL, 88, 3.0, 1.7, 15.0);

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
VALUES ('Молоко 3.2%', NULL, 60, 3.2, 3.6, 4.8);
