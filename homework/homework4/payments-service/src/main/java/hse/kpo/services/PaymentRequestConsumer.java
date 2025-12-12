package hse.kpo.services;

import hse.kpo.kafka.PaymentRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRequestConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "payment-requests", groupId = "payments-service")
    public void handlePaymentRequest(PaymentRequestedEvent event) {
        paymentService.processPaymentRequest(event);
    }
}
