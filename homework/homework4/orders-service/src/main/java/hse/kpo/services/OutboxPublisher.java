package hse.kpo.services;

import hse.kpo.domains.OutboxEvent;
import hse.kpo.kafka.PaymentRequestedEvent;
import hse.kpo.repositories.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, PaymentRequestedEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findBySentAtIsNullOrderByCreatedAtAsc();
        
        for (OutboxEvent event : pendingEvents) {
            try {
                PaymentRequestedEvent paymentEvent = objectMapper.readValue(
                        event.getPayload(), 
                        PaymentRequestedEvent.class
                );
                
                kafkaTemplate.send("payment-requests", event.getAggregateId(), paymentEvent);
                
                event.setSentAt(LocalDateTime.now());
                outboxEventRepository.save(event);
            } catch (Exception e) {
                event.setAttemptCount(event.getAttemptCount() + 1);
                event.setLastError(e.getMessage());
                outboxEventRepository.save(event);
            }
        }
    }
}
