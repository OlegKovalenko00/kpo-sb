package hse.kpo.services;

import hse.kpo.domains.OutboxEvent;
import hse.kpo.kafka.PaymentResultEvent;
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
    private final KafkaTemplate<String, PaymentResultEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findBySentAtIsNullOrderByCreatedAtAsc();
        
        for (OutboxEvent event : pendingEvents) {
            try {
                PaymentResultEvent resultEvent = objectMapper.readValue(
                        event.getPayload(), 
                        PaymentResultEvent.class
                );
                
                kafkaTemplate.send("payment-results", event.getAggregateId(), resultEvent);
                
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
