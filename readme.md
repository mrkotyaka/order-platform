# 📦 Order Platform (Microservices)

Система управления заказами, построенная на событийно-ориентированной архитектуре (Event-Driven Architecture) с использованием **Spring Boot 3**, **Java 21** и **Apache Kafka**.

## 🏗 Архитектура и Модули

- **common-libs** — Общие DTO, события (Events) и перечисления (Enums).
- **order-service** (:8087) — Оркестратор заказов. Создаёт записи и меняет статусы.
- **payment-service** (:8088) — Обработка транзакций.
- **delivery-service** (:8089) — Назначение курьеров и отслеживание доставки.

## 🔄 Взаимодействие через Kafka

Сервисы общаются асинхронно. Основная цепочка событий:

1. **Order Service** создаёт заказ и публикует событие `order-created`.
2. **Payment Service** слушает `order-created`, списывает средства и публикует `payment-completed`.
3. **Order Service** меняет статус заказа на `PAID` и публикует `order-paid`.
4. **Delivery Service** ловит `order-paid`, назначает курьера и публикует `delivery-assigned`.
5. **Order Service** обновляет финальный статус.

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
### Order Service (:8087)

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
    { "itemId": 1, "quantity": 2, "name": "ice cream" },
    { "itemId": 2, "quantity": 2, "name": "bread" }
  ]
}
```

**💳 Оплатить заказ по id**
```http 
POST http://localhost:8080/api/orders/pay/102

Authorization: Bearer токен, полученный при авторизации
```
```JSON
{
   "paymentMethod": "CARD"
}
```

**🔍 Найти заказ по id**
```http 
GET http://localhost:8080/api/orders/102

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

**🔍 Найти всех свободных курьеров**
```http 
GET http://localhost:8080/api/couriers

Authorization: Bearer токен, полученный при авторизации
```

## ⚙️ Технологии
- Runtime: Java 21, Spring Boot 3.5.7
- Data: Spring Data JPA, PostgreSQL, Hibernate 6 
- Messaging: Apache Kafka
- Build: Gradle (Multi-module)

## 🆘 Решение проблем
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
- ✔️ authentification-service
- ✔️ customers (id, name, email, phone, role) - implements like user_credentials
- close the direct method call. Stay only 8080
- notification-service. Sending email to customers and couriers. Use RabbitMQ (Redis). Use interface for methods
- cart-service (user-service, logging, create new)
- catalog-service (menu-service)
- items (id, name, price)
- implements Liquibase or Flyway (spring.jpa.hibernate.ddl-auto=validate)
- in order_items rename courier_name to courier_id. implements logic
- in orders rename address to delivery_address. implements logic
- make default for delivery_address - fill by default from customers.address if null
- impl items
- notification-service - check by null email
- get order - check customer login (id)
- deliveries rename deliveries.courier_name to deliveries.courier_id. Impl transfer courier_name by courier_id 
- add multithreading