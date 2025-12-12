package hse.kpo.kafka;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public record PaymentRequestedEvent(
        String messageId,
        String orderId,
        Long userId,
        BigDecimal amount
) {}
