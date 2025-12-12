package hse.kpo.dto.requests;

import java.math.BigDecimal;

public record TopupRequest(
        BigDecimal amount
) {}
