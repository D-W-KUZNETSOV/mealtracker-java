-- liquibase formatted sql
-- changeset dmitriy:20260926-45-seed-more-ingredients

-- ============================================================
-- ОВОЩИ
-- ============================================================
INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Помидор', 'SYSTEM', 20, 1.1, 0.2, 3.8
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Помидор' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Огурец', 'SYSTEM', 15, 0.8, 0.1, 2.8
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Огурец' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Капуста белокочанная', 'SYSTEM', 28, 1.8, 0.1, 4.7
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Капуста белокочанная' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Морковь', 'SYSTEM', 35, 1.3, 0.1, 6.9
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Морковь' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Лук репчатый', 'SYSTEM', 41, 1.4, 0.2, 8.2
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Лук репчатый' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Перец болгарский', 'SYSTEM', 27, 1.3, 0.1, 5.3
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Перец болгарский' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Кабачок', 'SYSTEM', 24, 0.6, 0.3, 4.6
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Кабачок' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Баклажан', 'SYSTEM', 24, 1.2, 0.1, 4.5
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Баклажан' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Брокколи', 'SYSTEM', 34, 2.8, 0.4, 6.6
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Брокколи' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Шпинат', 'SYSTEM', 22, 2.9, 0.3, 2.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Шпинат' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Салат листовой', 'SYSTEM', 15, 1.4, 0.2, 2.9
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Салат листовой' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Свекла', 'SYSTEM', 42, 1.5, 0.1, 8.8
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Свекла' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Редис', 'SYSTEM', 20, 1.2, 0.1, 3.4
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Редис' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Тыква', 'SYSTEM', 22, 1.0, 0.1, 4.4
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Тыква' AND username = 'SYSTEM');

-- ============================================================
-- ФРУКТЫ
-- ============================================================
INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Яблоко', 'SYSTEM', 52, 0.3, 0.2, 13.8
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Яблоко' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Банан', 'SYSTEM', 89, 1.1, 0.3, 22.8
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Банан' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Апельсин', 'SYSTEM', 47, 0.9, 0.1, 11.8
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Апельсин' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Груша', 'SYSTEM', 57, 0.4, 0.1, 15.2
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Груша' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Виноград', 'SYSTEM', 69, 0.7, 0.2, 18.1
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Виноград' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Клубника', 'SYSTEM', 32, 0.7, 0.3, 7.7
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Клубника' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Малина', 'SYSTEM', 52, 1.2, 0.7, 11.9
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Малина' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Черника', 'SYSTEM', 57, 0.7, 0.3, 14.5
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Черника' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Киви', 'SYSTEM', 61, 1.1, 0.5, 14.7
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Киви' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Лимон', 'SYSTEM', 29, 1.1, 0.3, 9.3
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Лимон' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Персик', 'SYSTEM', 39, 0.9, 0.3, 9.5
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Персик' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Абрикос', 'SYSTEM', 48, 1.4, 0.4, 11.1
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Абрикос' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Мандарин', 'SYSTEM', 53, 0.8, 0.3, 13.3
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Мандарин' AND username = 'SYSTEM');

-- ============================================================
-- КРУПЫ И МАКАРОНЫ
-- ============================================================
INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Гречка сухая', 'SYSTEM', 343, 12.6, 3.3, 62.1
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Гречка сухая' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Рис белый сухой', 'SYSTEM', 344, 6.7, 0.7, 78.9
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Рис белый сухой' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Макароны сухие', 'SYSTEM', 350, 12.5, 1.5, 72.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Макароны сухие' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Булгур', 'SYSTEM', 342, 12.3, 1.3, 75.9
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Булгур' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Кускус', 'SYSTEM', 376, 12.8, 0.6, 77.4
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Кускус' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Овсяные хлопья', 'SYSTEM', 366, 12.3, 6.2, 61.8
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Овсяные хлопья' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Манная крупа', 'SYSTEM', 333, 10.3, 1.0, 70.6
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Манная крупа' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Перловка', 'SYSTEM', 315, 9.3, 1.1, 73.7
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Перловка' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Пшено', 'SYSTEM', 348, 11.5, 3.3, 69.3
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Пшено' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Картофель', 'SYSTEM', 77, 2.0, 0.1, 17.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Картофель' AND username = 'SYSTEM');

-- ============================================================
-- МЯСО И ПТИЦА
-- ============================================================
INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Куриное бедро', 'SYSTEM', 185, 21.0, 11.0, 0.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Куриное бедро' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Куриная грудка варёная', 'SYSTEM', 137, 29.8, 1.8, 0.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Куриная грудка варёная' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Говядина', 'SYSTEM', 187, 18.9, 12.4, 0.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Говядина' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Свинина', 'SYSTEM', 259, 16.4, 21.6, 0.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Свинина' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Индейка', 'SYSTEM', 197, 21.6, 12.0, 0.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Индейка' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Фарш говяжий', 'SYSTEM', 254, 17.2, 20.0, 0.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Фарш говяжий' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Бекон', 'SYSTEM', 417, 12.0, 40.0, 0.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Бекон' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Колбаса варёная', 'SYSTEM', 257, 12.8, 22.2, 1.5
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Колбаса варёная' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Печень куриная', 'SYSTEM', 137, 20.4, 5.9, 1.1
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Печень куриная' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Сосиски', 'SYSTEM', 266, 11.0, 24.0, 1.5
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Сосиски' AND username = 'SYSTEM');

-- ============================================================
-- РЫБА И МОРЕПРОДУКТЫ
-- ============================================================
INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Лосось', 'SYSTEM', 208, 20.0, 13.0, 0.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Лосось' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Треска', 'SYSTEM', 82, 18.0, 0.7, 0.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Треска' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Тунец', 'SYSTEM', 132, 28.0, 1.0, 0.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Тунец' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Скумбрия', 'SYSTEM', 205, 18.0, 13.0, 0.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Скумбрия' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Сельдь', 'SYSTEM', 217, 17.0, 16.0, 0.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Сельдь' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Минтай', 'SYSTEM', 72, 16.0, 0.8, 0.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Минтай' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Креветки', 'SYSTEM', 99, 24.0, 0.3, 0.2
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Креветки' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Кальмар', 'SYSTEM', 92, 15.6, 1.4, 3.1
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Кальмар' AND username = 'SYSTEM');

-- ============================================================
-- МОЛОЧНЫЕ ПРОДУКТЫ
-- ============================================================
INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Творог 9%', 'SYSTEM', 159, 16.7, 9.0, 2.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Творог 9%' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Творог обезжиренный', 'SYSTEM', 71, 16.5, 0.5, 1.3
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Творог обезжиренный' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Сметана 20%', 'SYSTEM', 206, 2.5, 20.0, 3.4
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Сметана 20%' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Кефир 2.5%', 'SYSTEM', 50, 2.9, 2.5, 4.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Кефир 2.5%' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Йогурт натуральный', 'SYSTEM', 66, 3.5, 3.2, 4.9
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Йогурт натуральный' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Сыр твёрдый', 'SYSTEM', 364, 25.0, 29.0, 2.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Сыр твёрдый' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Моцарелла', 'SYSTEM', 280, 22.0, 22.0, 2.2
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Моцарелла' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Молоко 1.5%', 'SYSTEM', 44, 3.0, 1.5, 4.8
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Молоко 1.5%' AND username = 'SYSTEM');

-- ============================================================
-- ХЛЕБ И ВЫПЕЧКА
-- ============================================================
INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Хлеб белый', 'SYSTEM', 265, 8.0, 1.0, 51.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Хлеб белый' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Хлеб чёрный', 'SYSTEM', 214, 6.6, 1.2, 41.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Хлеб чёрный' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Хлеб цельнозерновой', 'SYSTEM', 247, 13.0, 4.2, 41.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Хлеб цельнозерновой' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Лаваш тонкий', 'SYSTEM', 236, 8.0, 1.0, 49.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Лаваш тонкий' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Батон', 'SYSTEM', 262, 7.5, 2.9, 50.9
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Батон' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Багет', 'SYSTEM', 274, 8.5, 1.0, 55.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Багет' AND username = 'SYSTEM');

-- ============================================================
-- ЖИРЫ И МАСЛА
-- ============================================================
INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Масло растительное', 'SYSTEM', 899, 0.0, 99.9, 0.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Масло растительное' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Масло оливковое', 'SYSTEM', 898, 0.0, 99.8, 0.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Масло оливковое' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Масло сливочное', 'SYSTEM', 748, 0.5, 82.5, 0.8
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Масло сливочное' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Майонез', 'SYSTEM', 680, 1.0, 75.0, 2.6
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Майонез' AND username = 'SYSTEM');

-- ============================================================
-- ОРЕХИ И СЕМЕЧКИ
-- ============================================================
INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Миндаль', 'SYSTEM', 579, 21.2, 49.9, 21.6
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Миндаль' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Грецкий орех', 'SYSTEM', 654, 15.2, 65.2, 13.7
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Грецкий орех' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Кешью', 'SYSTEM', 553, 18.2, 43.9, 30.2
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Кешью' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Арахис', 'SYSTEM', 567, 25.8, 49.2, 16.1
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Арахис' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Фундук', 'SYSTEM', 628, 15.0, 60.8, 16.7
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Фундук' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Семечки подсолнечные', 'SYSTEM', 584, 20.8, 51.5, 20.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Семечки подсолнечные' AND username = 'SYSTEM');

-- ============================================================
-- ПРОЧЕЕ
-- ============================================================
INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Сахар', 'SYSTEM', 387, 0.0, 0.0, 99.9
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Сахар' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Мёд', 'SYSTEM', 304, 0.3, 0.0, 82.4
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Мёд' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Соль', 'SYSTEM', 0, 0.0, 0.0, 0.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Соль' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Перец чёрный молотый', 'SYSTEM', 251, 10.4, 3.3, 38.7
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Перец чёрный молотый' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Паприка', 'SYSTEM', 282, 14.1, 12.9, 53.9
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Паприка' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Чеснок', 'SYSTEM', 149, 6.4, 0.5, 33.1
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Чеснок' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Имбирь', 'SYSTEM', 80, 1.8, 0.8, 15.8
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Имбирь' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Кукуруза консервированная', 'SYSTEM', 88, 3.3, 1.4, 15.6
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Кукуруза консервированная' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Горошек зелёный', 'SYSTEM', 73, 5.0, 0.2, 13.3
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Горошек зелёный' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Фасоль красная', 'SYSTEM', 292, 21.0, 1.6, 46.0
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Фасоль красная' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Оливки', 'SYSTEM', 145, 1.0, 15.3, 3.8
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Оливки' AND username = 'SYSTEM');

INSERT INTO ingredients (name, username, calories_per100g, proteins_per100g, fats_per100g, carbs_per100g)
SELECT 'Шоколад тёмный', 'SYSTEM', 546, 6.2, 35.4, 48.2
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Шоколад тёмный' AND username = 'SYSTEM');

-- rollback DELETE FROM ingredients WHERE username = 'SYSTEM' AND name IN (
--   'Помидор','Огурец','Капуста белокочанная','Морковь','Лук репчатый','Перец болгарский','Кабачок','Баклажан','Брокколи','Шпинат','Салат листовой','Свекла','Редис','Тыква',
--   'Яблоко','Банан','Апельсин','Груша','Виноград','Клубника','Малина','Черника','Киви','Лимон','Персик','Абрикос','Мандарин',
--   'Гречка сухая','Рис белый сухой','Мака