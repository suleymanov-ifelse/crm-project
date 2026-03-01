# CRM

Pet-проект CRM: **Java 21, Spring Boot, PostgreSQL, Docker, JUnit**.

## Требования

- Java 21
- Gradle 8.x (или используй `./gradlew` из репозитория)
- Docker и Docker Compose (для полного запуска)

## Запуск

### Вариант 1: Только БД в Docker, приложение локально

```bash
docker compose up -d postgres
./gradlew bootRun
```

### Вариант 2: Всё в Docker

```bash
docker compose up --build
```

Приложение: **http://localhost:8080**  
PostgreSQL: **localhost:5432** (user/pass: `crm`/`crm`, DB: `crm`)

## Тесты

```bash
./gradlew test
```

Используется профиль `test` и H2 in-memory — PostgreSQL не нужен.

## API (curl)

Базовый URL: `http://localhost:8080/api/customers`

### Список клиентов

```bash
curl -s http://localhost:8080/api/customers
```

### Создать клиента

```bash
curl -s -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Иван Петров","email":"ivan@example.com","phone":"+7 987 123-45-67"}'
```

### Получить клиента по id

```bash
curl -s http://localhost:8080/api/customers/1
```

### Обновить клиента

```bash
curl -s -X PUT http://localhost:8080/api/customers/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Иван Петров (обновлён)","email":"ivan.new@example.com","phone":"+7 987 000-00-00"}'
```

### Удалить клиента

```bash
curl -s -X DELETE http://localhost:8080/api/customers/1
```

### Счета клиента (OneToMany)

```bash
curl -s http://localhost:8080/api/customers/1/invoices
```

## Стек

- **Gradle** — сборка
- **Spring Boot 3.2** — Web, Data JPA
- **PostgreSQL** — БД
- **Lombok** — сущности
- **JUnit 5 + MockMvc** — тесты контроллера
- **Docker Compose** — postgres + app

## Структура

- `CrmApplication` — точка входа
- `entity/` — Customer, Invoice (OneToMany)
- `repository/` — JpaRepository
- `service/CustomerService` — бизнес-логика (SRP, DI)
- `CustomerController` — REST CRUD + `/customers/{id}/invoices`
