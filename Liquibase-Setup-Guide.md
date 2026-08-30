📋 Настройка Liquibase в Spring Boot 4.1.1
1️⃣ Добавление зависимости в pom.xml
xml
<properties>
<liquibase.version>4.29.2</liquibase.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.liquibase</groupId>
        <artifactId>liquibase-core</artifactId>
        <version>${liquibase.version}</version>
        <!-- НЕ добавляйте <scope>runtime</scope> -->
    </dependency>
</dependencies>
2️⃣ Настройка application.properties
properties
# Liquibase настройки
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.sql
spring.liquibase.enabled=true
spring.liquibase.drop-first=false
spring.liquibase.default-schema=public
spring.liquibase.database-change-log-table=databasechangelog
spring.liquibase.database-change-log-lock-table=databasechangeloglock

# Логирование для отладки
logging.level.liquibase=DEBUG
logging.level.liquibase.changelog=DEBUG
logging.level.liquibase.executor=DEBUG
logging.level.org.springframework.boot.autoconfigure.liquibase=DEBUG

# Отключите Hibernate DDL (важно!)
spring.jpa.hibernate.ddl-auto=none
3️⃣ Создание файла миграции
Путь: src/main/resources/db/changelog/db.changelog-master.sql
sql
-- liquibase formatted sql

-- changeset your_name:1
ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS fats_per100g DOUBLE PRECISION;
ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS proteins_per100g DOUBLE PRECISION;
ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS carbs_per100g DOUBLE PRECISION;

-- changeset your_name:2
ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS test_column VARCHAR(255);
Альтернатива - YAML формат:
Файл: src/main/resources/db/changelog/db.changelog-master.yaml

yaml
databaseChangeLog:
- changeSet:
  id: 1
  author: your_name
  changes:
  - addColumn:
  tableName: ingredients
  columns:
  - column:
  name: fats_per100g
  type: DOUBLE PRECISION
  - column:
  name: proteins_per100g
  type: DOUBLE PRECISION
  - column:
  name: carbs_per100g
  type: DOUBLE PRECISION
  В application.properties нужно указать:

properties
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml
4️⃣ Структура папок
text
src/
└── main/
├── java/
│   └── com/
│       └── your/
│           └── package/
└── resources/
├── application.properties
└── db/
└── changelog/
├── db.changelog-master.sql    ← основной файл
└── changes/                    ← опционально
└── add_macros.sql         ← если используете include
5️⃣ Команды для сборки и запуска
Windows (CMD):
cmd
mvn clean compile
mvn spring-boot:run
Windows (PowerShell):
powershell
mvn clean compile
mvn spring-boot:run
Linux/Mac:
bash
mvn clean compile
mvn spring-boot:run
6️⃣ Проверка в базе данных
sql
-- Проверить таблицу истории
SELECT * FROM databasechangelog;

-- Проверить блокировки
SELECT * FROM databasechangeloglock;

-- Проверить колонки
SELECT column_name, data_type
FROM information_schema.columns
WHERE table_name = 'ingredients';
7️⃣ Полезные настройки
Добавление нескольких changelog файлов через include:
db.changelog-master.sql:

sql
-- liquibase formatted sql

-- changeset your_name:include
-- include: changes/add_macros.sql
ИЛИ в YAML:

yaml
databaseChangeLog:
- include:
  file: changes/add_macros.sql
  relativeToChangelogFile: true
  Использование переменных:
  application.properties:

properties
spring.liquibase.parameters.table_name=ingredients
changelog.sql:

sql
-- liquibase formatted sql

-- changeset your_name:1
ALTER TABLE ${table_name} ADD COLUMN IF NOT EXISTS fats_per100g DOUBLE PRECISION;
8️⃣ Устранение неполадок
Проблема	Решение
Liquibase не запускается	Проверьте spring.liquibase.enabled=true
Ошибка "databasechangelog не существует"	Добавьте spring.liquibase.drop-first=true (только для разработки)
Конфликт с Hibernate	Установите spring.jpa.hibernate.ddl-auto=none
Не видно логов	Добавьте logging.level.liquibase=DEBUG
Ошибка пути к файлу	Проверьте путь: classpath:db/changelog/db.changelog-master.sql
Миграция не применяется	Проверьте changeset id - должен быть уникальным
9️⃣ Пример полного рабочего application.properties
properties
spring.application.name=mealtracker

# База данных
spring.datasource.url=jdbc:postgresql://localhost:5432/mealtracker_db
spring.datasource.username=dmitriy
spring.datasource.password=banana19

# Liquibase
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.sql
spring.liquibase.enabled=true
spring.liquibase.drop-first=false
spring.liquibase.default-schema=public

# Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# Логирование
logging.level.root=INFO
logging.level.liquibase=DEBUG
logging.level.org.springframework.boot.autoconfigure.liquibase=DEBUG
logging.level.org.hibernate.SQL=DEBUG
🔟 Важные заметки
Версии: Liquibase 4.29.2+ работают с Spring Boot 4.1.1

Порядок: Liquibase запускается ДО Hibernate

Разработка: Используйте drop-first=true только в разработке

Продакшн: Всегда устанавливайте drop-first=false

Имена changeset: Используйте уникальные id и автор