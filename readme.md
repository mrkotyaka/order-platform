# 📦 Order Platform (Microservices)

Система управления заказами, построенная на событийно-ориентированной архитектуре (Event-Driven Architecture) с использованием **Spring Boot 3**, **Java 21** и **Apache Kafka**.

## 🏗 Архитектура и Модули

- **gateway-service** — (:8080) AuthenticationFilter и application.yaml с настройкой маршрутизации.
- **auth-service** — (:8081) регистрация и авторизация пользователя.
- **common-libs** — Общие DTO, события (Events) и перечисления (Enums).
- **order-service** (:8087) — Оркестратор заказов. Создаёт записи и меняет статусы.
- **payment-service** (:8088) — Обработка транзакций.
- **delivery-service** (:8089) — Назначение курьеров и отслеживание доставки.

## 🔄 Взаимодействие через Kafka

Сервисы общаются асинхронно. Основная цепочка событий:

1. **Order Service** создаёт заказ и публикует событие `order-created`.
1. **Payment Service** слушает `order-created`, списывает средства и публикует `payment-completed`.
1. **Order Service** меняет статус заказа на `PAID` и публикует `order-paid`.
1. **Delivery Service** ловит `order-paid`, назначает курьера и публикует `delivery-assigned`.
1. **Order Service** обновляет финальный статус.

### Схема топиков и сервисов

| Topic            | Producer         | Consumers        |
|------------------|------------------|------------------|
| `order-events`   | order-service    | delivery-service |
| `delivery-events`| delivery-service | order-service    |

## 🚀 Быстрый запуск

### 1. Запуск инфраструктуры
   Для работы проекта необходимы PostgreSQL и Kafka. Запуск через Docker:
   ```Bash
   docker-compose up -d
   ```
### 2. Сборка проекта
   ```Bash
   ./gradlew clean build
   ```
## 🛠 REST API
### Gateway (:8080)

**🟢 Регистрация**

```http 
POST http://localhost:8080/api/auth/register
Content-Type: application/json
```
```JSON
{
  "username": "логин",
  "password": "пароль"
}
```

**🟢 Логин**

```http 
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```
```JSON
{
  "username": "логин",
  "password": "пароль"
}
```

**🟢 Создать заказ**

```http 
POST http://localhost:8080/api/orders

Authorization: Bearer токен, полученный при авторизации
```

```JSON
{
  "address": "mira, 12",
  "items": [
    { "quantity": 2, "name": "ice cream" },
    { "quantity": 2, "name": "bread" }
  ]
}
```

**🔍 Найти заказ по id**
```http 
GET http://localhost:8080/api/orders/{1}

Authorization: Bearer токен, полученный при авторизации
```

**💳 Оплатить заказ по id**
```http 
POST http://localhost:8080/api/orders/pay/{1}

Authorization: Bearer токен, полученный при авторизации
```
```JSON
{
   "paymentMethod": "CARD"
}
```

**🔍 Найти все товары**
```http 
GET http://localhost:8080/api/items

Authorization: Bearer токен, полученный при авторизации
```

**🔍 Найти всех клиентов**
```http 
GET http://localhost:8080/api/customers

Authorization: Bearer токен, полученный при авторизации
```

**🔍 Найти все заказы**
```http 
GET http://localhost:8080/api/orders

Authorization: Bearer токен, полученный при авторизации
```

**🔍 Найти все заказы, ожидающие оплаты**
```http 
GET http://localhost:8080/api/orders/pendingpayment

Authorization: Bearer токен, полученный при авторизации
```

**🔍 Найти всех свободных курьеров**
```http 
GET http://localhost:8080/api/couriers

Authorization: Bearer токен, полученный при авторизации
```

**🔍 Найти инфо по текущему клиенту**
```http 
GET http://localhost:8080/api/customers/whoami

Authorization: Bearer токен, полученный при авторизации
```

**🔍 Найти инфо по id клиента**
```http 
GET http://localhost:8080/api/customers/{1}

Authorization: Bearer токен, полученный при авторизации
```

## ⚙️ Технологии
- Runtime: Java 21, Spring Boot 3.5.7
- Data: Spring Data JPA, PostgreSQL, Hibernate 6 
- Messaging: Apache Kafka
- Build: Gradle (Multi-module)

## 🆘 Решение проблем (for memory)
**Ошибка:** violates check constraint "orders_order_status_check"

**Причина:** в Java добавлен новый статус в Enum, а в PostgreSQL осталось старое ограничение.

**Решение:**
```SQL
ALTER TABLE orders DROP CONSTRAINT orders_order_status_check;
```

## 📝 План для реализации
### Add
- ✔️ common-libs
- ✔️ order-service
- ✔️ payment-service
- ✔️ delivery-service
- ✔️ couriers (id, name, email, phone)
- ✔️ impl couriers
- ✔️ assign free courier for an order
- ✔️ add multithreading
- ✔️ authentification-service
- ✔️ gateway-service
- ✔️ customers (id, name, email, phone, role) - implements like user_credentials
- ✔️ impl items (id, name, price)
- ✔️ get order - check customer login (id)
- notification-service. Sending email to customers and couriers. Use RabbitMQ (Redis). Use interface for methods
- notification-service - check by null email
- add into customers card boolean type of notice (sms, email)
- cart-service (user-service, logging, create new)
- catalog-service (menu-service)
- in order_items rename courier_name to courier_id. implements logic
- implements Liquibase or Flyway (spring.jpa.hibernate.ddl-auto=validate)
- deliveries rename deliveries.courier_name to deliveries.courier_id. Impl transfer courier_name by courier_id 
- close the direct method call. Stay only 8080 in docker in the end
