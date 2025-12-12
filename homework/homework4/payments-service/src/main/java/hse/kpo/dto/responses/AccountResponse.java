package hse.kpo.dto.responses;

import java.math.BigDecimal;

public record AccountResponse(
        Long userId,
        BigDecimal balance
) {}
