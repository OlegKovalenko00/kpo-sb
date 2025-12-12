package hse.kpo.dto.requests;

import java.math.BigDecimal;

public record CreateOrderRequest(
        BigDecimal amount,
        String description
) {}
