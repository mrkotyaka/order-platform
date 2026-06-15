# 📦 Order Platform (Microservices)

Система управления заказами, построенная на событийно-ориентированной архитектуре (Event-Driven Architecture) с использованием **Spring Boot 3**, **Java 21** и **Apache Kafka**.

## 🏗 Архитектура и Модули

- **gateway-service** (:8080) — AuthenticationFilter и application.yaml с настройкой маршрутизации.
- **auth-service** (:8081) — регистрация и авторизация пользователя.
- **common-libs** — Общие DTO, события (Events) и перечисления (Enums).
- **order-service** (:8087) — Оркестратор заказов. Создаёт записи и меняет статусы.
- **payment-service** (:8088) — Обработка транзакций.
- **delivery-service** (:8089) — Назначение курьеров и отслеживание доставки.
- **notification-service** (:8090) — Отправка уведомлений.
- **review-service** (:8091) — Отзывы с рейтингом по заказу, курьеру(доставка) и продуктам.

## 🔄 Взаимодействие через Kafka

Сервисы общаются асинхронно. Основная цепочка событий:

1. **Order Service** создаёт заказ и публикует событие `order-created`.
2. **Payment Service** слушает `order-created`, списывает средства и публикует `payment-completed`.
3. **Order Service** меняет статус заказа на `PAID` и публикует `order-paid`.
4. **Delivery Service** ловит `order-paid`, назначает курьера и публикует `delivery-assigned`.
5. **Order Service** обновляет финальный статус.
6. **Delivery Service** меняет статус заказа на `DELIVERED`.
7. **Notification Service** формирует и оправляет уведомления клиенту по событиям: `ORDER_CREATED`, `PAYMENT_SUCCESS`, `PAYMENT_FAILED`, `COURIER_ASSIGNED`.
8. **Review Service** формирует и сохраняет отзывы с рейтингом по заказу, курьеру(доставка) и продуктам. Формирует средний рейтинг для курьера.
9. **[MailHog](http://localhost:8025/)** - позволяет проверить отправку уведомлений на email.


### Схема топиков и сервисов

| Topic             | Producer         | Consumers        |
|-------------------|------------------|------------------|
| `order-events`    | order-service    | delivery-service |
| `delivery-events` | delivery-service | order-service    |

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

<details>
<summary> Регистрация и авторизация </summary>

**🔑 Регистрация**

```http 
POST http://localhost:8080/api/auth/register
Content-Type: application/json
```
```JSON
{
   "username": "логин",
   "password": "пароль",
   "address": "адрес",
   "email": "э-мейл",
   "phone": "номер телефона",
   "notificationPreference": "SMS/EMAIL/PUSH"
}
```

**✅ Логин**

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

**🔍 Найти всех клиентов**
```http 
GET http://localhost:8080/api/customers

Authorization: Bearer токен, полученный при авторизации
```

**🔍 Найти инфо по текущему пользователю**
```http 
GET http://localhost:8080/api/customers/whoami

Authorization: Bearer токен, полученный при авторизации
```

**🔍 Найти инфо по id клиента**
```http 
GET http://localhost:8080/api/customers/{userId}

Authorization: Bearer токен, полученный при авторизации
```

</details>

<details>
<summary> Товары </summary>

**📝 Создать список товаров**
```http 
POST http://localhost:8080/api/items

Authorization: Bearer токен, полученный при авторизации
```
```JSON
[
   {
      "name": "продукт",
      "price": double
   }
]
```

**🔍 Найти все товары**
```http 
GET http://localhost:8080/api/items

Authorization: Bearer токен, полученный при авторизации
```

</details>

<details>
<summary> Заказы </summary>

**📦 Создать заказ**

```http 
POST http://localhost:8080/api/orders

Authorization: Bearer токен, полученный при авторизации
```

```JSON
{
  "address": "адрес доставки",
  "items": [
    { 
       "quantity": int,
       "name": "продукт"
    },
    { 
       "quantity": int,
       "name": "продукт"
    }
  ]
}
```

**🔍 Найти заказ по id**
```http 
GET http://localhost:8080/api/orders/{orderId}

Authorization: Bearer токен, полученный при авторизации
```

**💳 Оплатить заказ по id**
```http 
POST http://localhost:8080/api/orders/pay/{orderId}

Authorization: Bearer токен, полученный при авторизации
```
```JSON
{
   "paymentMethod": "CARD/QR/YANDEX_SPLIT"
}
```

**❌ Отменить заказ по id**
```http 
POST http://localhost:8080/api/orders/cancel/{orderId}

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

**🔍 Найти заказы по статусу**
```http 
GET http://localhost:8080/api/orders/status?orderStatus=DELIVERY_ASSIGNED

Authorization: Bearer токен, полученный при авторизации
```

| Статусы           |
|-------------------|
| PENDING_PAYMENT   |
| PAID              |
| PAYMENT_FAILED    |
| DELIVERY_ASSIGNED |
| DELIVERED         |
| CANCELED          |


</details>

<details>
<summary> Доставка </summary>

**🚚 Заказ доставлен**

```http 
POST http://localhost:8080/api/deliveries/delivered/{1}
Content-Type: application/json
```

</details>

<details>
<summary> Курьеры </summary>

**🔍 Найти всех свободных курьеров**
```http 
GET http://localhost:8080/api/couriers

Authorization: Bearer токен, полученный при авторизации
```

</details>

<details>
<summary> Отзывы </summary>

**💬 Создать отзыв**

```http 
POST http://localhost:8080/api/reviews

Authorization: Bearer токен, полученный при авторизации
```

```JSON
{
   "orderId": "orderId",
   "orderRating": int (от 1 до 5),
   "courierRating": int (от 1 до 5),
   "productRating": int (от 1 до 5),
   "comment": "комментарий" (от 1 до 300 символов)
}
```

**🔍 Найти все отзывы**

```http 
GET http://localhost:8080/api/reviews

Authorization: Bearer токен, полученный при авторизации
```

**🔍 Найти отзыв по orderId**

```http 
GET http://localhost:8080/api/reviews/order{orderId}

Authorization: Bearer токен, полученный при авторизации
```

**🔍 Найти отзыв по reviewId**

```http 
GET http://localhost:8080/api/reviews/{reviewId}

Authorization: Bearer токен, полученный при авторизации
```

</details>

## ⚙️ Технологии
- Runtime: Java 21, Spring Boot 3.5.7
- Data: Spring Data JPA, PostgreSQL, Hibernate 6 
- Messaging: Apache Kafka
- Build: Gradle (Multi-module)

## 🆘 Решение проблем (for memory)
**Ошибка:** violates check constraint "orders_order_status_check"

**Причина:** в Java добавлен новый статус в Enum, а в PostgreSQL осталось старое ограничение.

**Решение:**
``` SQL
ALTER TABLE orders DROP CONSTRAINT orders_order_status_check;
```

**Ошибка:** Спам в логе в сервисе доставки: `The class 'ru.mrkotyaka.commonlibs.kafka.delivery.OrderPaidEvent' is not in the trusted packages:
[java.util, java.lang, ru.mrkotyaka.commonlibs.kafka]`

**Причина:** Пакет ru.mrkotyaka.commonlibs.kafka.delivery не совпадает с указанным ru.mrkotyaka.commonlibs.kafka.

**Решение:** В KafkaConfiguration delivery-service:
``` java
props.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.mrkotyaka.commonlibs.kafka.delivery");
// или разрешить всё сразу:
props.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.mrkotyaka.commonlibs.*");
```

**Ошибка:** Спам в логе в сервисе доставки: `This error handler cannot process 'SerializationException's directly; please consider configuring an 'ErrorHandlingDeserializer' in the value and/or key deserializer`

**Причина:** в том что в топике order.events лежит старое сообщение на offset 30, которое delivery-service не может десериализовать — и пытается снова и снова.

**Решение:** Пропустить застрявший offset — старое сообщение уже не десериализуется, его нужно пропустить. Порядок исправления:
1. Остановить **delivery-service**.
2. Сбросить offset:
    ```bash
    docker exec -it <kafka-container> kafka-consumer-groups --bootstrap-server localhost:9092 --group delivery-service-group --topic order.events --reset-offsets --to-latest --execute
    ```
3. Запустить **delivery-service**.

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
- ✔️ get order - check user login (id)
- ✔️ notification-service
- ✔️ add into customers card boolean type of notice (sms, email, push)
- ✔️ fix assign courier!
- ✔️ notification-service. Sending email to customers. Used Kafka
- ✔️ notification-service - check by null email
- ✔️ deliveries rename deliveries.courier_name to deliveries.courier_id. Impl transfer courier_name by courier_id
- ✔️ implementation Liquibase (spring.jpa.hibernate.ddl-auto=validate)
- ✔️ check DELIVERY_ASSIGNED for delivered
- ✔️ add methods for create records couriers and items
- ✔️ implementation auth for courier into auth-service and transfer common data into delivery.courierEntity via Kafka for assign.
- ✔️ transfer external rests
- ✔️ notification-service. Sending email to couriers.
- ✔️ add rest cancel
- ✔️ add rest by status
- ✔️ update ddl for liquibase
- ✔️ add reviews-service (feedback). Different rating for delivery, system and products
- order picking simulation, can not cancel
- add customers description to order
- Later. close the direct method call. Stay only 8080 in docker in the end