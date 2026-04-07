package com.agrobasis.core_service.pricing.api.dto;

import com.agrobasis.core_service.market.domain.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExchangeRateSnapshotResponse(
        Currency fromCurrency,
        Currency toCurrency,
        BigDecimal rate,
        String source,
        LocalDateTime quotedAt
) {
}
