package hse.kpo.services;

import hse.kpo.domains.Order;
import hse.kpo.domains.OrderStatus;
import hse.kpo.domains.OutboxEvent;
import hse.kpo.domains.ProcessedMessage;
import hse.kpo.dto.requests.CreateOrderRequest;
import hse.kpo.dto.responses.OrderResponse;
import hse.kpo.kafka.PaymentRequestedEvent;
import hse.kpo.kafka.PaymentResultEvent;
import hse.kpo.repositories.OrderRepository;
import hse.kpo.repositories.OutboxEventRepository;
import hse.kpo.repositories.ProcessedMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ProcessedMessageRepository processedMessageRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        Order order = Order.builder()
                .userId(userId)
                .amount(request.amount())
                .description(request.description())
                .status(OrderStatus.NEW)
                .build();
        
        order = orderRepository.save(order);

        PaymentRequestedEvent paymentEvent = new PaymentRequestedEvent(
                UUID.randomUUID().toString(),
                order.getId().toString(),
                userId,
                request.amount()
        );

        try {
            String payload = objectMapper.writeValueAsString(paymentEvent);
            
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateId(order.getId().toString())
                    .eventType("PaymentRequested")
                    .payload(payload)
                    .build();
            
            outboxEventRepository.save(outboxEvent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return toResponse(order);
    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<OrderResponse> getOrderById(UUID orderId) {
        return orderRepository.findById(orderId).map(this::toResponse);
    }

    @Transactional
    public void processPaymentResult(PaymentResultEvent event) {
        if (processedMessageRepository.existsByMessageId(event.messageId())) {
            return;
        }

        ProcessedMessage processedMessage = ProcessedMessage.builder()
                .messageId(event.messageId())
                .receivedAt(LocalDateTime.now())
                .build();
        
        try {
            processedMessageRepository.save(processedMessage);
        } catch (Exception e) {
            return;
        }

        UUID orderId = UUID.fromString(event.orderId());
        orderRepository.findById(orderId).ifPresent(order -> {
            if (order.getStatus() == OrderStatus.NEW) {
                if (event.success()) {
                    order.setStatus(OrderStatus.FINISHED);
                } else {
                    order.setStatus(OrderStatus.CANCELLED);
                }
                orderRepository.save(order);
            }
        });
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getAmount(),
                order.getDescription(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }
}
