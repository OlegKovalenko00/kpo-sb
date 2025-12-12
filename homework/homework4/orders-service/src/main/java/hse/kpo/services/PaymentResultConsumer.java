package hse.kpo.services;

import hse.kpo.kafka.PaymentResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentResultConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "payment-results", groupId = "orders-service")
    public void handlePaymentResult(PaymentResultEvent event) {
        orderService.processPaymentResult(event);
    }
}
