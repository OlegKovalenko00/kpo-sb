package hse.kpo.services;

import hse.kpo.domains.*;
import hse.kpo.kafka.PaymentRequestedEvent;
import hse.kpo.kafka.PaymentResultEvent;
import hse.kpo.repositories.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final AccountRepository accountRepository;
    private final InboxEventRepository inboxEventRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final PaymentDebitRepository paymentDebitRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void processPaymentRequest(PaymentRequestedEvent event) {
        if (inboxEventRepository.existsByMessageId(event.messageId())) {
            return;
        }

        try {
            InboxEvent inboxEvent = InboxEvent.builder()
                    .messageId(event.messageId())
                    .payload(objectMapper.writeValueAsString(event))
                    .build();
            inboxEventRepository.save(inboxEvent);
        } catch (Exception e) {
            return;
        }

        Optional<PaymentDebit> existingDebit = paymentDebitRepository.findByOrderId(event.orderId());
        if (existingDebit.isPresent()) {
            return;
        }

        Optional<Account> accountOpt = accountRepository.findByUserId(event.userId());
        
        PaymentResultEvent resultEvent;
        String resultMessageId = UUID.randomUUID().toString();
        
        if (accountOpt.isEmpty()) {
            PaymentDebit debit = PaymentDebit.builder()
                    .orderId(event.orderId())
                    .userId(event.userId())
                    .amount(event.amount())
                    .status(DebitStatus.FAILED)
                    .failureReason("Account not found")
                    .build();
            paymentDebitRepository.save(debit);
            
            resultEvent = new PaymentResultEvent(
                    resultMessageId,
                    event.orderId(),
                    false,
                    "Account not found"
            );
        } else {
            Account account = accountOpt.get();
            
            if (account.getBalance().compareTo(event.amount()) < 0) {
                PaymentDebit debit = PaymentDebit.builder()
                        .orderId(event.orderId())
                        .userId(event.userId())
                        .amount(event.amount())
                        .status(DebitStatus.FAILED)
                        .failureReason("Insufficient balance")
                        .build();
                paymentDebitRepository.save(debit);
                
                resultEvent = new PaymentResultEvent(
                        resultMessageId,
                        event.orderId(),
                        false,
                        "Insufficient balance"
                );
            } else {
                int updated = accountRepository.debitBalance(
                        account.getUserId(),
                        event.amount(),
                        account.getVersion()
                );
                
                if (updated == 0) {
                    throw new RuntimeException("Concurrent modification detected");
                }
                
                PaymentDebit debit = PaymentDebit.builder()
                        .orderId(event.orderId())
                        .userId(event.userId())
                        .amount(event.amount())
                        .status(DebitStatus.SUCCESS)
                        .build();
                paymentDebitRepository.save(debit);
                
                resultEvent = new PaymentResultEvent(
                        resultMessageId,
                        event.orderId(),
                        true,
                        "Payment successful"
                );
            }
        }

        try {
            String payload = objectMapper.writeValueAsString(resultEvent);
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateId(event.orderId())
                    .eventType("PaymentResult")
                    .payload(payload)
                    .build();
            outboxEventRepository.save(outboxEvent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
