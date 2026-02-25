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

**🟢 Создать заказ**

```http 
POST http://localhost:8087/api/orderss
```
```JSON
{
  "customerId": 2,
  "address": "mira, 12",
  "items": [
    { "itemId": 1, "quantity": 2, "name": "ice cream" },
    { "itemId": 2, "quantity": 2, "name": "bread" }
  ]
}
```
**🔵 Найти заказ**
```http 
GET /api/orders/2
```
**💳 Оплатить заказ**
```http 
POST /api/orders/2/pay
```
```JSON
{
   "paymentMethod": "CARD"
}
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
- authentification-service
- notification-service. Sending email to customers and couriers. Use RabbitMQ
- cart-service
- catalog-service
- items (id, name, price)
- customers (id, name, email, phone)
- couriers (id, name, email, phone)
- implements Liquibase or Flyway (spring.jpa.hibernate.ddl-auto=validate)
