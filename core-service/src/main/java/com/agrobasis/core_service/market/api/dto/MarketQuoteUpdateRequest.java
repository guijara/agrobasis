package com.agrobasis.core_service.market.api.dto;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.Unit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MarketQuoteUpdateRequest(
        @Schema(description = "Commodity cotada", example = "CORN")
        @NotNull(message = "A commodity é obrigatória")
        Commodity commodity,

        @Schema(description = "Origem da cotação de mercado", example = "B3")
        @NotBlank(message = "A origem da cotação é obrigatória")
        String source,

        @Schema(description = "Preço da commodity", example = "98.10")
        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser maior que zero")
        BigDecimal price,

        @Schema(description = "Moeda da cotação", example = "BRL")
        @NotNull(message = "A moeda é obrigatória")
        Currency currency,

        @Schema(description = "Unidade da cotação", example = "toneladas")
        @NotNull(message = "A unidade é obrigatória")
        Unit unit,

        @Schema(description = "Data e hora da cotação", example = "2026-04-07T10:15:00")
        @NotNull(message = "A data da cotação é obrigatória")
        LocalDateTime quotedAt
) {
}
