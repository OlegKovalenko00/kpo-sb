# Homework 4 - Async Microservices

## Architecture

```
┌─────────────────┐     ┌──────────────────┐     ┌───────────────────┐
│   API Gateway   │────▶│  Orders Service  │────▶│ Payments Service  │
│    (port 8080)  │     │   (port 8081)    │     │   (port 8082)     │
└─────────────────┘     └──────────────────┘     └───────────────────┘
                               │                         │
                               ▼                         ▼
                        ┌────────────┐           ┌────────────┐
                        │ Orders DB  │           │Payments DB │
                        │(port 5432) │           │(port 5433) │
                        └────────────┘           └────────────┘
                               │                         │
                               └──────────┬──────────────┘
                                          ▼
                                   ┌────────────┐
                                   │   Kafka    │
                                   │(port 9092) │
                                   └────────────┘
```

## Services

### API Gateway (port 8080)
Routes requests to orders and payments services.

### Orders Service (port 8081)
- Creates orders with status NEW
- Uses Transactional Outbox pattern to publish PaymentRequested events
- Consumes PaymentResult events to update order status (FINISHED/CANCELLED)

### Payments Service (port 8082)
- Creates and manages user accounts
- Processes payments with exactly-once guarantee
- Uses Transactional Inbox for deduplication
- Uses Transactional Outbox to publish PaymentResult events

## Database Tables

### Orders DB
- orders: id, user_id, amount, description, status, created_at, updated_at
- outbox_events: id, aggregate_id, event_type, payload, created_at, sent_at, attempt_count, last_error
- processed_messages: id, message_id, received_at

### Payments DB
- accounts: user_id, balance, version, created_at, updated_at
- inbox_events: id, message_id, received_at, payload
- outbox_events: id, aggregate_id, event_type, payload, created_at, sent_at, attempt_count, last_error
- payment_debits: id, order_id, user_id, amount, status, failure_reason, created_at

## Kafka Topics
- payment-requests: PaymentRequested events from orders-service
- payment-results: PaymentResult events from payments-service

## Running

```bash
cd homework4
docker compose up --build
```

Wait for all services to start (approximately 1-2 minutes).

## Ports
- API Gateway: http://localhost:8080
- Orders Service: http://localhost:8081
- Payments Service: http://localhost:8082
- Orders DB: localhost:5432
- Payments DB: localhost:5433
- Kafka: localhost:9092

## Testing

### Using curl

1. Create account:
```bash
curl -X POST http://localhost:8080/api/payments/account \
  -H "X-User-Id: 1"
```

2. Top up account:
```bash
curl -X POST http://localhost:8080/api/payments/account/topup \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{"amount": 1000.00}'
```

3. Check balance:
```bash
curl http://localhost:8080/api/payments/account/balance \
  -H "X-User-Id: 1"
```

4. Create order:
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{"amount": 100.00, "description": "Test order"}'
```

5. Get orders:
```bash
curl http://localhost:8080/api/orders \
  -H "X-User-Id: 1"
```

6. Get order status (replace ORDER_ID):
```bash
curl http://localhost:8080/api/orders/ORDER_ID \
  -H "X-User-Id: 1"
```

### Using Postman
Import postman_collection.json and set variables:
- baseUrl: http://localhost:8080
- userId: 1
- orderId: (set after creating an order)

## Swagger
- Orders Service: http://localhost:8081/swagger-ui.html
- Payments Service: http://localhost:8082/swagger-ui.html

## Guarantees
- At-least-once message delivery via Kafka
- Exactly-once payment processing via:
  - Transactional Inbox for message deduplication
  - Unique constraint on order_id in payment_debits table
  - Optimistic locking (version field) for account balance updates
