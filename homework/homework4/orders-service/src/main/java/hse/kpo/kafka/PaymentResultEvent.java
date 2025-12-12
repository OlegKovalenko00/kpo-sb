package hse.kpo.kafka;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public record PaymentResultEvent(
        String messageId,
        String orderId,
        boolean success,
        String reason
) {}
