# ДЗ 4 - Асинхронные Микросервисы

## Описание

Система обработки заказов и платежей, реализованная в виде асинхронных микросервисов с использованием паттернов **Transactional Outbox/Inbox** для гарантированной доставки сообщений и **exactly-once** обработки платежей.

## Архитектура

```
┌─────────────────┐     ┌──────────────────┐     ┌───────────────────┐
│   Frontend      │     │  Orders Service  │◀───▶│ Payments Service  │
│   (port 3000)   │     │   (port 8081)    │     │   (port 8082)     │
└────────┬────────┘     └────────┬─────────┘     └─────────┬─────────┘
         │                       │                         │
         │              ┌────────┴─────────┐               │
         │              │   API Gateway    │               │
         └─────────────▶│   (port 8080)    │◀──────────────┘
                        └────────┬─────────┘
                                 │
         ┌───────────────────────┼───────────────────────┐
         ▼                       ▼                       ▼
   ┌───────────┐          ┌───────────┐          ┌───────────┐
   │ Orders DB │          │   Kafka   │          │Payments DB│
   │(port 5432)│          │(port 9092)│          │(port 5433)│
   └───────────┘          └───────────┘          └───────────┘
```

### Сервисы

| Сервис | Порт | Описание |
|--------|------|----------|
| **Frontend** | 3000 | Веб-интерфейс с WebSocket уведомлениями |
| **API Gateway** | 8080 | Маршрутизация запросов к микросервисам |
| **Orders Service** | 8081 | Управление заказами, Transactional Outbox |
| **Payments Service** | 8082 | Управление счетами и платежами |

### Базы данных

| База данных | Порт | Описание |
|-------------|------|----------|
| **orders_db** | 5432 | Заказы, outbox события, обработанные сообщения |
| **payments_db** | 5433 | Счета, inbox/outbox события, дебеты |

## Технологии

- **Java 21** + **Spring Boot 3.4.2**
- **Apache Kafka** - асинхронная коммуникация
- **PostgreSQL** - хранение данных
- **Spring Cloud Gateway** - API Gateway
- **WebSocket + STOMP** - real-time уведомления
- **Docker** + **Docker Compose** - контейнеризация
- **OpenAPI/Swagger** - документация API

## Запуск

### Требования

- Docker Desktop (Windows/macOS) или Docker Engine (Linux)
- Docker Compose v2+
- Минимум 4GB свободной RAM

### Linux / macOS (Bash/Zsh)

```bash
# Переход в директорию проекта
cd homework4

# Сборка и запуск всех сервисов
docker compose up --build

# Запуск в фоновом режиме
docker compose up --build -d

# Просмотр логов
docker compose logs -f

# Остановка всех сервисов
docker compose down

# Остановка и удаление данных (volumes)
docker compose down -v
```

### Windows (PowerShell)

```powershell
# Переход в директорию проекта
cd homework4

# Сборка и запуск всех сервисов
docker compose up --build

# Запуск в фоновом режиме
docker compose up --build -d

# Просмотр логов
docker compose logs -f

# Остановка всех сервисов
docker compose down

# Остановка и удаление данных (volumes)
docker compose down -v
```

### Windows (CMD)

```cmd
REM Переход в директорию проекта
cd homework4

REM Сборка и запуск всех сервисов
docker compose up --build

REM Запуск в фоновом режиме
docker compose up --build -d

REM Просмотр логов
docker compose logs -f

REM Остановка всех сервисов
docker compose down

REM Остановка и удаление данных (volumes)
docker compose down -v
```

> **Время запуска:** ~1-2 минуты для первой сборки

## Endpoints

### API Gateway (http://localhost:8080)

| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | `/api/payments/account` | Создать счёт |
| POST | `/api/payments/account/topup` | Пополнить счёт |
| GET | `/api/payments/account/balance` | Получить баланс |
| POST | `/api/orders` | Создать заказ |
| GET | `/api/orders` | Получить список заказов |
| GET | `/api/orders/{id}` | Получить заказ по ID |

> **Заголовок:** Все запросы требуют `X-User-Id: <userId>`

## Тестирование

### Веб-интерфейс (Frontend)

Откройте браузер: **http://localhost:3000**

Функции:
- Создание счёта
- Пополнение баланса
- Просмотр баланса
- Создание заказов
- Просмотр списка заказов
- **WebSocket уведомления** о смене статуса заказа в реальном времени

### Тестирование через curl

#### Linux / macOS

```bash
# 1. Создать счёт
curl -X POST http://localhost:8080/api/payments/account \
  -H "X-User-Id: 1"

# 2. Пополнить счёт на 1000
curl -X POST http://localhost:8080/api/payments/account/topup \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{"amount": 1000.00}'

# 3. Проверить баланс
curl http://localhost:8080/api/payments/account/balance \
  -H "X-User-Id: 1"

# 4. Создать заказ на 100
curl -X POST http://localhost:8080/api/orders \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{"amount": 100.00, "description": "Тестовый заказ"}'

# 5. Получить список заказов
curl http://localhost:8080/api/orders \
  -H "X-User-Id: 1"

# 6. Получить конкретный заказ (замените ORDER_ID)
curl http://localhost:8080/api/orders/ORDER_ID \
  -H "X-User-Id: 1"
```

#### Windows (PowerShell)

```powershell
# 1. Создать счёт
Invoke-RestMethod -Uri "http://localhost:8080/api/payments/account" `
  -Method Post `
  -Headers @{"X-User-Id"="1"}

# 2. Пополнить счёт на 1000
Invoke-RestMethod -Uri "http://localhost:8080/api/payments/account/topup" `
  -Method Post `
  -Headers @{"X-User-Id"="1"; "Content-Type"="application/json"} `
  -Body '{"amount": 1000.00}'

# 3. Проверить баланс
Invoke-RestMethod -Uri "http://localhost:8080/api/payments/account/balance" `
  -Headers @{"X-User-Id"="1"}

# 4. Создать заказ на 100
Invoke-RestMethod -Uri "http://localhost:8080/api/orders" `
  -Method Post `
  -Headers @{"X-User-Id"="1"; "Content-Type"="application/json"} `
  -Body '{"amount": 100.00, "description": "Тестовый заказ"}'

# 5. Получить список заказов
Invoke-RestMethod -Uri "http://localhost:8080/api/orders" `
  -Headers @{"X-User-Id"="1"}

# 6. Получить конкретный заказ (замените ORDER_ID)
Invoke-RestMethod -Uri "http://localhost:8080/api/orders/ORDER_ID" `
  -Headers @{"X-User-Id"="1"}
```

#### Windows (CMD + curl)

```cmd
REM 1. Создать счёт
curl -X POST http://localhost:8080/api/payments/account -H "X-User-Id: 1"

REM 2. Пополнить счёт на 1000
curl -X POST http://localhost:8080/api/payments/account/topup -H "X-User-Id: 1" -H "Content-Type: application/json" -d "{\"amount\": 1000.00}"

REM 3. Проверить баланс
curl http://localhost:8080/api/payments/account/balance -H "X-User-Id: 1"

REM 4. Создать заказ на 100
curl -X POST http://localhost:8080/api/orders -H "X-User-Id: 1" -H "Content-Type: application/json" -d "{\"amount\": 100.00, \"description\": \"Test order\"}"

REM 5. Получить список заказов
curl http://localhost:8080/api/orders -H "X-User-Id: 1"
```

### Тестирование через Postman

1. Импортируйте `postman_collection.json`
2. Установите переменные:
   - `baseUrl`: `http://localhost:8080`
   - `userId`: `1`
   - `orderId`: (установите после создания заказа)

## Swagger UI

- **Orders Service:** http://localhost:8081/swagger-ui.html
- **Payments Service:** http://localhost:8082/swagger-ui.html

## Статусы заказов

| Статус | Описание |
|--------|----------|
| `NEW` | Заказ создан, ожидает оплаты |
| `FINISHED` | Оплата успешна |
| `CANCELLED` | Оплата отклонена (недостаточно средств) |

## Гарантии надёжности

### Transactional Outbox Pattern
- События сохраняются в БД в той же транзакции, что и бизнес-данные
- Фоновый процесс отправляет события в Kafka
- Гарантирует at-least-once доставку

### Transactional Inbox Pattern
- Все входящие сообщения записываются в inbox таблицу
- Дедупликация по `message_id`
- Предотвращает повторную обработку

### Exactly-Once обработка платежей
- Уникальный индекс на `order_id` в таблице `payment_debits`
- При повторной попытке списания возвращается ошибка unique constraint
- Оптимистическая блокировка (поле `version`) для баланса счёта

### Конкурентные операции с балансом
- Compare-and-Swap (CAS) через `version` поле
- При конфликте - повторная попытка с актуальным балансом

## Kafka Topics

| Topic | Источник | Потребитель | Описание |
|-------|----------|-------------|----------|
| `payment-requests` | Orders | Payments | Запросы на оплату |
| `payment-results` | Payments | Orders | Результаты оплаты |

## WebSocket

### Подключение
```
ws://localhost:8081/ws
```

### Подписка на обновления заказов
```
/topic/orders/{userId}
```

### Формат сообщений
```json
{
  "id": "uuid",
  "userId": 1,
  "amount": 100.00,
  "description": "Тестовый заказ",
  "status": "FINISHED",
  "createdAt": "2024-01-01T12:00:00",
  "updatedAt": "2024-01-01T12:00:05"
}
```

## Структура проекта

```
homework4/
├── docker-compose.yml       # Конфигурация Docker Compose
├── postman_collection.json  # Коллекция Postman
├── README.md               # Документация
├── api-gateway/            # API Gateway сервис
├── orders-service/         # Сервис заказов
├── payments-service/       # Сервис платежей
└── frontend/               # Веб-интерфейс
```

## Troubleshooting

### Сервисы не запускаются

```bash
# Проверьте логи
docker compose logs

# Перезапустите с пересборкой
docker compose down -v
docker compose up --build
```

### Порты заняты

Убедитесь, что порты 3000, 8080, 8081, 8082, 5432, 5433, 9092 свободны:

**Linux/macOS:**
```bash
lsof -i :8080
```

**Windows (PowerShell):**
```powershell
netstat -ano | findstr :8080
```

### Недостаточно памяти

Увеличьте память Docker до минимум 4GB в настройках Docker Desktop.

---

## Критерии оценивания и их реализация

### Сводная таблица

| Критерий | Баллы | Статус | Где реализовано |
|----------|-------|--------|-----------------|
| Функциональность | 2 |  | API endpoints |
| Архитектура | 5 |  | Outbox/Inbox, exactly-once, CAS |
| Docker | 0.5 |  | Dockerfile, docker-compose.yml |
| Postman коллекция | 0.5 |  | postman_collection.json |
| **Базовый итог** | **8** |  | |
| Фронтенд + WebSocket | +2 |  | frontend/, WebSocket |
| **Итого** | **10** |  | |

---

### Функциональность (2 балла) 

#### Payments Service - API для работы со счетами

| Endpoint | Описание | Файл |
|----------|----------|------|
| `POST /api/payments/account` | Создание счёта | `PaymentController.java` |
| `POST /api/payments/account/topup` | Пополнение счёта | `PaymentController.java` |
| `GET /api/payments/account/balance` | Получение баланса | `PaymentController.java` |

**Расположение:** `payments-service/src/main/java/ru/hse/payments/controller/PaymentController.java`

#### Orders Service - API для работы с заказами

| Endpoint | Описание | Файл |
|----------|----------|------|
| `POST /api/orders` | Создание заказа | `OrderController.java` |
| `GET /api/orders` | Список заказов пользователя | `OrderController.java` |
| `GET /api/orders/{id}` | Получение заказа по ID | `OrderController.java` |

**Расположение:** `orders-service/src/main/java/ru/hse/orders/controller/OrderController.java`

#### Как проверить:
```bash
# Linux/macOS
curl -X POST http://localhost:8080/api/payments/account -H "X-User-Id: 1"
curl -X POST http://localhost:8080/api/payments/account/topup -H "X-User-Id: 1" -H "Content-Type: application/json" -d '{"amount": 1000}'
curl http://localhost:8080/api/payments/account/balance -H "X-User-Id: 1"
curl -X POST http://localhost:8080/api/orders -H "X-User-Id: 1" -H "Content-Type: application/json" -d '{"amount": 100, "description": "Test"}'
curl http://localhost:8080/api/orders -H "X-User-Id: 1"
```

```powershell
# Windows PowerShell
Invoke-RestMethod -Uri "http://localhost:8080/api/payments/account" -Method Post -Headers @{"X-User-Id"="1"}
Invoke-RestMethod -Uri "http://localhost:8080/api/payments/account/topup" -Method Post -Headers @{"X-User-Id"="1"; "Content-Type"="application/json"} -Body '{"amount": 1000}'
Invoke-RestMethod -Uri "http://localhost:8080/api/payments/account/balance" -Headers @{"X-User-Id"="1"}
Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Headers @{"X-User-Id"="1"; "Content-Type"="application/json"} -Body '{"amount": 100, "description": "Test"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Headers @{"X-User-Id"="1"}
```

---

### Архитектура (5 баллов) 

#### 2.1 Transactional Outbox Pattern (at-least-once delivery)

**Описание:** События сохраняются в таблицу `outbox_events` в той же транзакции, что и бизнес-данные. Фоновый процесс периодически отправляет события в Kafka.

**Реализация в Orders Service:**
- `orders-service/src/main/java/ru/hse/orders/entity/OutboxEvent.java` - сущность события
- `orders-service/src/main/java/ru/hse/orders/repository/OutboxEventRepository.java` - репозиторий
- `orders-service/src/main/java/ru/hse/orders/service/OutboxPublisher.java` - публикатор событий (Scheduled task)

**Реализация в Payments Service:**
- `payments-service/src/main/java/ru/hse/payments/entity/OutboxEvent.java`
- `payments-service/src/main/java/ru/hse/payments/repository/OutboxEventRepository.java`
- `payments-service/src/main/java/ru/hse/payments/service/OutboxPublisher.java`

**Как работает:**
1. При создании заказа событие `PaymentRequested` записывается в `outbox_events`
2. `OutboxPublisher` каждые 100ms проверяет таблицу и отправляет события в Kafka
3. После успешной отправки событие помечается как отправленное (`sent_at`)

#### 2.2 Transactional Inbox Pattern (дедупликация)

**Описание:** Все входящие сообщения записываются в таблицу `inbox_events` перед обработкой. Дедупликация по `message_id`.

**Реализация:**
- `payments-service/src/main/java/ru/hse/payments/entity/InboxEvent.java`
- `payments-service/src/main/java/ru/hse/payments/repository/InboxEventRepository.java`
- `payments-service/src/main/java/ru/hse/payments/kafka/PaymentRequestConsumer.java` - проверка дубликатов

**Как работает:**
```java
// PaymentRequestConsumer.java
if (inboxEventRepository.existsByMessageId(messageId)) {
    log.info("Duplicate message detected: {}", messageId);
    return; // Пропускаем дубликат
}
inboxEventRepository.save(new InboxEvent(messageId, payload));
```

#### 2.3 Exactly-Once обработка платежей

**Описание:** Уникальный индекс на `order_id` в таблице `payment_debits` гарантирует, что каждый заказ будет оплачен только один раз.

**Реализация:**
- `payments-service/src/main/java/ru/hse/payments/entity/PaymentDebit.java` - поле `orderId` с `@Column(unique = true)`
- `payments-service/src/main/resources/db/migration/V1__init.sql` - `UNIQUE(order_id)`

**SQL схема:**
```sql
CREATE TABLE payment_debits (
    id UUID PRIMARY KEY,
    order_id UUID UNIQUE NOT NULL,  -- Гарантирует exactly-once
    user_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    failure_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);
```

#### 2.4 Конкурентные операции с балансом (Compare-and-Swap)

**Описание:** Оптимистическая блокировка через поле `version` предотвращает race conditions при параллельных операциях с балансом.

**Реализация:**
- `payments-service/src/main/java/ru/hse/payments/entity/Account.java` - поле `@Version`
- `payments-service/src/main/java/ru/hse/payments/service/AccountService.java` - метод `debitBalance()`

**Как работает:**
```java
// Account.java
@Version
private Long version;

// AccountService.java - debitBalance использует UPDATE с WHERE version = ?
@Modifying
@Query("UPDATE Account a SET a.balance = a.balance - :amount, a.version = a.version + 1 WHERE a.userId = :userId AND a.version = :version AND a.balance >= :amount")
int debitBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount, @Param("version") Long version);
```

При конкурентном доступе один из запросов получит 0 обновлённых строк и будет повторён с актуальной версией.

---

### Docker (0.5 балла) 

**Файлы:**
- `docker-compose.yml` - оркестрация всех сервисов
- `api-gateway/Dockerfile` - образ API Gateway
- `orders-service/Dockerfile` - образ Orders Service
- `payments-service/Dockerfile` - образ Payments Service
- `frontend/Dockerfile` - образ Frontend (nginx)

**Multi-stage сборка:**
```dockerfile
# Stage 1: Build
FROM gradle:8.11.1-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# Stage 2: Run
FROM eclipse-temurin:21-jre
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Как проверить:**
```bash
# Сборка и запуск
docker compose up --build

# Проверка статуса контейнеров
docker compose ps
```

---

### Postman коллекция (0.5 балла) 

**Файл:** `postman_collection.json`

**Содержит запросы:**
1. Create Account
2. Top Up Account
3. Get Balance
4. Create Order
5. Get Orders
6. Get Order by ID

**Как использовать:**
1. Откройте Postman
2. File → Import → выберите `postman_collection.json`
3. Установите переменные в Collections → Variables:
   - `baseUrl`: `http://localhost:8080`
   - `userId`: `1`
4. Выполняйте запросы последовательно

---

### Бонус: Frontend + WebSocket (+2 балла) 

#### Frontend (веб-интерфейс)

**Расположение:** `frontend/`
- `frontend/index.html` - HTML/CSS/JS интерфейс
- `frontend/Dockerfile` - nginx контейнер
- `frontend/nginx.conf` - конфигурация nginx

**URL:** http://localhost:3000

**Функционал:**
- Создание счёта
- Пополнение баланса
- Просмотр баланса
- Создание заказов
- Просмотр списка заказов
- Real-time уведомления через WebSocket

#### WebSocket уведомления

**Реализация в Orders Service:**
- `orders-service/src/main/java/ru/hse/orders/config/WebSocketConfig.java` - конфигурация STOMP
- `orders-service/src/main/java/ru/hse/orders/service/OrderNotificationService.java` - отправка уведомлений

**Endpoint:** `ws://localhost:8081/ws`

**Подписка:** `/topic/orders/{userId}`

**Как работает:**
1. Frontend подключается к WebSocket при загрузке страницы
2. При изменении статуса заказа (NEW → FINISHED/CANCELLED) сервер отправляет уведомление
3. Frontend получает уведомление и обновляет UI в реальном времени

**Код подключения (JavaScript):**
```javascript
const socket = new SockJS('http://localhost:8081/ws');
const stompClient = Stomp.over(socket);
stompClient.connect({}, function(frame) {
    stompClient.subscribe('/topic/orders/' + userId, function(message) {
        const order = JSON.parse(message.body);
        // Обновление UI
    });
});
```

**Как проверить:**
1. Откройте http://localhost:3000
2. Введите User ID (например, 1)
3. Нажмите "Connect WebSocket"
4. Создайте счёт и пополните его
5. Создайте заказ
6. Наблюдайте как статус заказа автоматически обновляется с NEW на FINISHED

---

## Итоговая проверка всех критериев

### Чек-лист для проверяющего:

| # | Критерий | Как проверить | Ожидаемый результат |
|---|----------|---------------|---------------------|
| 1 | API Payments | `POST /api/payments/account` | Счёт создан |
| 2 | API Payments | `POST /api/payments/account/topup` | Баланс увеличился |
| 3 | API Payments | `GET /api/payments/account/balance` | Возвращает баланс |
| 4 | API Orders | `POST /api/orders` | Заказ создан со статусом NEW |
| 5 | API Orders | `GET /api/orders` | Список заказов |
| 6 | API Orders | `GET /api/orders/{id}` | Заказ по ID |
| 7 | Outbox | Проверить таблицу `outbox_events` | События записываются |
| 8 | Inbox | Проверить таблицу `inbox_events` | Сообщения дедуплицируются |
| 9 | Exactly-once | Повторить сообщение в Kafka | Второе списание не произойдёт |
| 10 | CAS | Параллельные запросы на списание | Нет race condition |
| 11 | Docker | `docker compose up --build` | Все сервисы запущены |
| 12 | Postman | Импорт коллекции | Все запросы работают |
| 13 | Frontend | http://localhost:3000 | UI отображается |
| 14 | WebSocket | Создать заказ | Статус обновляется в реальном времени |

### Команды для полной проверки flow:

**Linux/macOS:**
```bash
# 1. Запуск
docker compose up --build -d

# 2. Дождаться запуска (30 сек)
sleep 30

# 3. Создать счёт
curl -X POST http://localhost:8080/api/payments/account -H "X-User-Id: 1"

# 4. Пополнить на 1000
curl -X POST http://localhost:8080/api/payments/account/topup \
  -H "X-User-Id: 1" -H "Content-Type: application/json" \
  -d '{"amount": 1000}'

# 5. Проверить баланс (должно быть 1000)
curl http://localhost:8080/api/payments/account/balance -H "X-User-Id: 1"

# 6. Создать заказ на 100
curl -X POST http://localhost:8080/api/orders \
  -H "X-User-Id: 1" -H "Content-Type: application/json" \
  -d '{"amount": 100, "description": "Test order"}'

# 7. Подождать обработки (2 сек)
sleep 2

# 8. Проверить заказы (статус должен быть FINISHED)
curl http://localhost:8080/api/orders -H "X-User-Id: 1"

# 9. Проверить баланс (должно быть 900)
curl http://localhost:8080/api/payments/account/balance -H "X-User-Id: 1"
```

**Windows PowerShell:**
```powershell
# 1. Запуск
docker compose up --build -d

# 2. Дождаться запуска
Start-Sleep -Seconds 30

# 3. Создать счёт
Invoke-RestMethod -Uri "http://localhost:8080/api/payments/account" -Method Post -Headers @{"X-User-Id"="1"}

# 4. Пополнить на 1000
Invoke-RestMethod -Uri "http://localhost:8080/api/payments/account/topup" -Method Post -Headers @{"X-User-Id"="1"; "Content-Type"="application/json"} -Body '{"amount": 1000}'

# 5. Проверить баланс (должно быть 1000)
Invoke-RestMethod -Uri "http://localhost:8080/api/payments/account/balance" -Headers @{"X-User-Id"="1"}

# 6. Создать заказ на 100
Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Headers @{"X-User-Id"="1"; "Content-Type"="application/json"} -Body '{"amount": 100, "description": "Test order"}'

# 7. Подождать обработки
Start-Sleep -Seconds 2

# 8. Проверить заказы (статус должен быть FINISHED)
Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Headers @{"X-User-Id"="1"}

# 9. Проверить баланс (должно быть 900)
Invoke-RestMethod -Uri "http://localhost:8080/api/payments/account/balance" -Headers @{"X-User-Id"="1"}
```