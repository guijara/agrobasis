package com.agrobasis.core_service.market.api.dto;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.Unit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MarketQuoteResponse(
        UUID id,
        Commodity commodity,
        String source,
        BigDecimal price,
        Currency currency,
        Unit unit,
        LocalDateTime quotedAt,
        LocalDateTime createdAt
) {
}
