package com.agrobasis.core_service.market.api.dto;

import com.agrobasis.core_service.market.domain.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ExchangeRateResponse(
        UUID id,
        Currency fromCurrency,
        Currency toCurrency,
        BigDecimal rate,
        String source,
        LocalDateTime quotedAt,
        LocalDateTime createdAt
) {
}
