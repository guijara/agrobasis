package com.agrobasis.core_service.market.api.dto;

import com.agrobasis.core_service.market.domain.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExchangeRateCreateRequest(
        @Schema(description = "Moeda de origem", example = "USD")
        @NotNull(message = "A moeda de origem é obrigatória")
        Currency fromCurrency,

        @Schema(description = "Moeda de destino", example = "BRL")
        @NotNull(message = "A moeda de destino é obrigatória")
        Currency toCurrency,

        @Schema(description = "Valor da taxa de câmbio", example = "5.421300")
        @NotNull(message = "A taxa é obrigatória")
        @Positive(message = "A taxa deve ser maior que zero")
        BigDecimal rate,

        @Schema(description = "Origem da taxa", example = "Banco Central")
        @NotBlank(message = "A origem é obrigatória")
        String source,

        @Schema(description = "Data e hora da cotação", example = "2026-04-07T10:00:00")
        @NotNull(message = "A data da cotação é obrigatória")
        LocalDateTime quotedAt
) {
}
