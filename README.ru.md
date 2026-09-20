# MealTracker

🇷🇺 **Русский** | 🇬🇧 [English version](README.md)

> Приложение для подсчёта КБЖУ продуктов, расчёта суточной нормы
> с учётом физической активности и установленных целей.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-blue.svg)](https://www.postgresql.org/)
[![Liquibase](https://img.shields.io/badge/Liquibase-4.29-blue.svg)](https://www.liquibase.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## О проекте

MealTracker — REST API для тех, кто хочет контролировать питание.
Помогает:

- вести личную книгу рецептов с автоматическим расчётом КБЖУ,
- отслеживать, что съедено за день,
- видеть, сколько белков, жиров и углеводов уже получено,
- получать суточную норму калорий с учётом параметров тела и активности.

API спроектирован под SPA-фронтенд (в разработке).

---

## Возможности

- **Аутентификация** — регистрация и вход по JWT (Bearer token).
- **Ингредиенты** — полный CRUD, поиск, набор базовых (системных) ингредиентов.
- **Рецепты** — создание из ингредиентов, авторасчёт КБЖУ, сводка,
  публичные/приватные, доступ по владельцу.
- **Ингредиенты в рецепте** — добавление и изменение веса.
- **Цели по питанию** — вес, белок на кг, целевые калории, уровень активности;
  расчёт целевого белка.
- **Профиль** — рост, вес, пол, дата рождения, активность;
  суточная норма калорий (формула Миффлина–Сан Жеора + коэффициент активности).
- **Дневник питания** — добавление порций, агрегированная статистика за день
  или по дате.
- **Статистика рецепта** — сводка КБЖУ.
- **Загрузка картинок** — для рецептов (multipart).
- **Swagger UI** — интерактивная документация API из коробки.

---

## Стек

| Слой          | Технология                                        |
|---------------|---------------------------------------------------|
| Язык          | Java 17+                                           |
| Фреймворк     | Spring Boot 4.1.1                                  |
| Web           | Spring MVC, Tomcat 11                              |
| Безопасность  | Spring Security 7, JJWT 0.12                       |
| Персистенс    | Spring Data JPA, Hibernate 7                       |
| Миграции      | Liquibase 4.29                                     |
| БД            | PostgreSQL 14+ (prod), H2 (профиль local)          |
| Документация  | springdoc-openapi 3.1 (Swagger UI)                 |
| Сборка        | Maven                                              |
| Boilerplate   | Lombok                                             |

---

## Архитектура

Слоистая архитектура с чётким разделением:

```
com.e.mealtracker
├── config         — конфигурация Spring, DataInitializer
├── controller     — REST-эндпоинты
├── domain         — JPA-сущности (Recipe, Ingredient, UserGoals, ...)
├── dto            — request/response модели
├── entity         — User, UserProfile, Role
├── exception      — кастомные исключения, GlobalExceptionHandler
├── repository     — Spring Data JPA репозитории
├── security       — JWT-фильтр, JwtService, SecurityConfig
├── service        — бизнес-логика
└── util           — enum'ы и хелперы (ActivityLevel, Gender, ...)
```

---

## Запуск

### Требования

- **JDK 17+** (проект компилируется в Java 17; протестирован на JDK 22)
- **Maven 3.9+**
- **PostgreSQL 14+** — только для prod-профиля. Для local используется
  in-memory H2, настройка не требуется.

### Клонирование

```bash
git clone https://github.com/D-W-KUZNETSOV/mealtracker-java.git
cd mealtracker-java/mealtracker/mealtracker
```

### Локальный запуск (H2, без настройки)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Что происходит:

- H2 in-memory создаётся автоматически.
- Liquibase накатывает миграции.
- Загружаются демо-данные (`app.init-demo-data=true` в local-профиле).
- Swagger UI: http://localhost:8080/swagger-ui.html

### Запуск с PostgreSQL

1. Создай `.env` в корне модуля:

   ```env
   DB_URL=jdbc:postgresql://localhost:5432/mealtracker_db
   DB_USERNAME=твой_пользователь
   DB_PASSWORD=твой_пароль
   JWT_SECRET=<base64url-секрет, минимум 32 байта>
   APP_INIT_DEMO_DATA=false
   ```

   Шаблон — в `.env.example`.

2. Собрать и запустить:

   ```bash
   mvn clean package
   java -jar target/mealtracker-0.0.1-SNAPSHOT.jar
   ```

   Или просто:

   ```bash
   mvn spring-boot:run
   ```

3. Swagger UI: http://localhost:8080/swagger-ui.html

### Генерация JWT-секрета

```bash
# PowerShell
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$b = New-Object byte[] 32
$rng.GetBytes($b)
[Convert]::ToBase64String($b).Replace('+','-').Replace('/', '_').TrimEnd('=')
```

---

## API

Базовый путь: `/api`. Полная интерактивная документация — `/swagger-ui.html`.

### Auth — `/api/auth`

| Метод | Путь        | Описание                                  |
|-------|-------------|-------------------------------------------|
| POST  | `/login`    | Вход, возвращает `{ token, type }`        |
| POST  | `/register` | Регистрация, возвращает 201               |

### Ingredients — `/api/ingredients`

| Метод  | Путь              | Описание                            |
|--------|-------------------|-------------------------------------|
| GET    | `/`               | Список ингредиентов пользователя    |
| POST   | `/`               | Создать ингредиент                  |
| GET    | `/base`           | Список базовых (системных)          |
| GET    | `/base/search`    | Поиск базовых по имени              |
| PUT    | `/{id}`           | Обновить ингредиент                 |
| DELETE | `/{id}`           | Удалить ингредиент                  |

### Recipes — `/api/recipes`

| Метод  | Путь                     | Описание                              |
|--------|--------------------------|---------------------------------------|
| GET    | `/`                      | Список рецептов (пагинация)           |
| POST   | `/`                      | Создать рецепт с ингредиентами        |
| GET    | `/{id}/summary`          | Сводка рецепта                        |
| GET    | `/public`                | Публичные рецепты                     |
| DELETE | `/{id}`                  | Удалить рецепт                        |
| PATCH  | `/{id}/visibility`       | Переключить public/private            |

### Recipe ingredients — `/api/recipes/{recipeId}/ingredients`

| Метод | Путь                  | Описание                          |
|-------|-----------------------|-----------------------------------|
| POST  | `/`                   | Добавить ингредиент в рецепт      |
| PUT   | `/{ingredientId}`     | Обновить вес ингредиента          |

### Recipe stats — `/api/recipes/{recipeId}/stats`

| Метод | Путь | Описание               |
|-------|------|------------------------|
| GET   | `/`  | КБЖУ-статистика рецепта|

### Nutrition — `/api/nutrition`

| Метод | Путь              | Описание                               |
|-------|-------------------|----------------------------------------|
| POST  | `/goals`          | Установить/обновить цели питания       |
| GET   | `/goals`          | Получить текущие цели                  |
| GET   | `/target-protein` | Рассчитать целевой белок               |

### Profile — `/api/profile`

| Метод | Путь                | Описание                          |
|-------|---------------------|-----------------------------------|
| GET   | `/`                 | Текущий профиль                   |
| PUT   | `/`                 | Обновить профиль                  |
| GET   | `/calories/daily`   | Суточная норма калорий            |

### Stats — `/api/stats`

| Метод | Путь           | Описание                          |
|-------|----------------|-----------------------------------|
| POST  | `/daily/add`   | Добавить порцию за сегодня        |
| GET   | `/daily`       | Агрегированная статистика за день |
| GET   | `/daily/{date}`| Статистика за конкретную дату     |

### Images — `/api/images`

| Метод | Путь      | Описание                  |
|-------|-----------|---------------------------|
| POST  | `/upload` | Загрузить картинку        |

### Аутентификация

Все эндпоинты, кроме `/api/auth/**`, требуют заголовок:

```
Authorization: Bearer <token>
```

---

## Тесты

```bash
mvn test
```

Проект содержит **39 unit-тестов** на сервисный слой
(`RecipeService`, `IngredientService`, `StatsService`, `DataInitializer`)
с Mockito. Также есть smoke-тест контекста приложения.

---

## Безопасность

- Секреты (доступ к БД, JWT-секрет) передаются через переменные окружения.
- `.env` **не** коммитится в git. Шаблон — `.env.example`.
- В `local`-профиле содержатся **только для разработки** секреты,
  действительные лишь на localhost и непригодные для прода.
- JWT подписывается HMAC-SHA секретом длиной не менее 32 байт.

---

## Структура проекта

```
mealtracker-java/
├── mealtracker/
│   └── mealtracker/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/e/mealtracker/    ← код приложения
│       │   │   └── resources/
│       │   │       ├── db/changelog/          ← миграции Liquibase
│       │   │       ├── application.properties
│       │   │       └── application-local.properties
│       │   └── test/java/com/e/mealtracker/   ← тесты
│       ├── pom.xml
│       └── .env.example
├── LICENSE
├── README.md
└── README.ru.md
```

---

## Roadmap

- [x] REST API (auth, рецепты, ингредиенты, дневник, профиль)
- [x] Миграции Liquibase
- [x] JWT-аутентификация
- [x] Unit-тесты сервисного слоя
- [x] Swagger-документация
- [ ] SPA-фронтенд
- [ ] CORS для SPA
- [ ] Эндпоинт `GET /api/auth/me`
- [ ] Docker

---

## Лицензия

Проект под лицензией MIT — см. файл [LICENSE](LICENSE).