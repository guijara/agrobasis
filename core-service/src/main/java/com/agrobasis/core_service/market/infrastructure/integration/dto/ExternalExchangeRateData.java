package com.agrobasis.core_service.market.infrastructure.integration.dto;

import com.agrobasis.core_service.market.domain.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExternalExchangeRateData(
        Currency fromCurrency,
        Currency toCurrency,
        BigDecimal rate,
        String source,
        LocalDateTime quotedAt
) {
}
