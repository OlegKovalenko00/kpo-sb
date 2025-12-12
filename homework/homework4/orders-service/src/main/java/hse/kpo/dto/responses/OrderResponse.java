package hse.kpo.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        Long userId,
        BigDecimal amount,
        String description,
        String status,
        LocalDateTime createdAt
) {}
