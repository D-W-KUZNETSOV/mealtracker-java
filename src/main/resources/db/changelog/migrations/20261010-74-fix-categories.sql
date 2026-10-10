--liquibase formatted sql

--changeset dmitriy:20261010-74-fix-categories
-- Исправление категорий у старых ингредиентов

-- MEAT
UPDATE ingredients SET category = 'MEAT'
WHERE username = 'SYSTEM' AND name IN (
  'Бекон', 'Говядина', 'Индейка', 'Колбаса варёная',
  'Куриная грудка (сырая)', 'Куриная грудка варёная',
  'Куриная грудка гриль', 'Куриная грудка жареная', 'Куриная грудка тушёная',
  'Куриное бедро варёное', 'Куриное бедро гриль', 'Куриное бедро жареное',
  'Печень куриная', 'Свинина', 'Сосиски', 'Фарш говяжий'
);
--rollback UPDATE ingredients SET category = 'OTHER' WHERE username = 'SYSTEM' AND category = 'MEAT';

-- FISH
UPDATE ingredients SET category = 'FISH'
WHERE username = 'SYSTEM' AND name IN (
  'Кальмар', 'Креветки', 'Лосось', 'Минтай', 'Сельдь', 'Скумбрия', 'Треска', 'Тунец'
);
--rollback UPDATE ingredients SET category = 'OTHER' WHERE username = 'SYSTEM' AND category = 'FISH';

-- VEGETABLES
UPDATE ingredients SET category = 'VEGETABLES'
WHERE username = 'SYSTEM' AND name IN (
  'Баклажан', 'Брокколи', 'Горошек зелёный', 'Имбирь', 'Кабачок',
  'Капуста белокочанная', 'Картофель', 'Лук репчатый', 'Морковь',
  'Огурец', 'Огурец свежий', 'Перец болгарский', 'Помидор', 'Редис',
  'Салат листовой', 'Свекла', 'Тыква', 'Чеснок', 'Шпинат'
);
--rollback UPDATE ingredients SET category = 'OTHER' WHERE username = 'SYSTEM' AND category = 'VEGETABLES';

-- FRUITS
UPDATE ingredients SET category = 'FRUITS'
WHERE username = 'SYSTEM' AND name IN (
  'Абрикос', 'Апельсин', 'Банан', 'Виноград', 'Груша', 'Киви',
  'Клубника', 'Лимон', 'Малина', 'Мандарин', 'Персик', 'Черника', 'Яблоко'
);
--rollback UPDATE ingredients SET category = 'OTHER' WHERE username = 'SYSTEM' AND category = 'FRUITS';

-- GRAINS
UPDATE ingredients SET category = 'GRAINS'
WHERE username = 'SYSTEM' AND name IN (
  'Багет', 'Батон', 'Булгур', 'Гречка варёная', 'Гречка сухая',
  'Кускус', 'Лаваш тонкий', 'Макароны сухие', 'Манная крупа',
  'Овсянка на воде', 'Овсяные хлопья (сухие)', 'Перловка', 'Пшено',
  'Рис белый сухой', 'Рис варёный', 'Хлеб белый', 'Хлеб цельнозерновой', 'Хлеб чёрный'
);
--rollback UPDATE ingredients SET category = 'OTHER' WHERE username = 'SYSTEM' AND category = 'GRAINS';

-- DAIRY
UPDATE ingredients SET category = 'DAIRY'
WHERE username = 'SYSTEM' AND name IN (
  'Йогурт натуральный', 'Кефир 2.5%', 'Молоко 1.5%', 'Молоко 3.2%',
  'Моцарелла', 'Сметана 20%', 'Сыр твёрдый',
  'Творог 5%', 'Творог 9%', 'Творог обезжиренный'
);
--rollback UPDATE ingredients SET category = 'OTHER' WHERE username = 'SYSTEM' AND category = 'DAIRY';

-- EGGS 🆕
UPDATE ingredients SET category = 'EGGS'
WHERE username = 'SYSTEM' AND name IN (
  'Яйцо куриное (среднее)', 'Яйцо куриное С0 (крупное)',
  'Яйцо куриное С1 (среднее)', 'Яйцо куриное С2 (мелкое)'
);
--rollback UPDATE ingredients SET category = 'OTHER' WHERE username = 'SYSTEM' AND category = 'EGGS';

-- NUTS 🆕
UPDATE ingredients SET category = 'NUTS'
WHERE username = 'SYSTEM' AND name IN (
  'Арахис', 'Грецкий орех', 'Кешью', 'Миндаль', 'Семечки подсолнечные', 'Фундук'
);
--rollback UPDATE ingredients SET category = 'OTHER' WHERE username = 'SYSTEM' AND category = 'NUTS';

-- OILS 🆕
UPDATE ingredients SET category = 'OILS'
WHERE username = 'SYSTEM' AND name IN (
  'Майонез', 'Масло оливковое', 'Масло растительное', 'Масло сливочное'
);
--rollback UPDATE ingredients SET category = 'OTHER' WHERE username = 'SYSTEM' AND category = 'OILS';

-- SPICES
UPDATE ingredients SET category = 'SPICES'
WHERE username = 'SYSTEM' AND name IN ('Паприка', 'Перец чёрный молотый', 'Соль');
--rollback UPDATE ingredients SET category = 'OTHER' WHERE username = 'SYSTEM' AND category = 'SPICES';

-- SWEETS
UPDATE ingredients SET category = 'SWEETS'
WHERE username = 'SYSTEM' AND name IN ('Мёд', 'Сахар', 'Шоколад тёмный');
--rollback UPDATE ingredients SET category = 'OTHER' WHERE username = 'SYSTEM' AND category = 'SWEETS';

-- OTHER (оставляем как есть)
-- Кукуруза консервированная, Оливки, Фасоль красная — уже OTHER ✅