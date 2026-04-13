package com.agrobasis.core_service.cost.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CommercialAdjustmentProfileUpdateRequest(
        @Schema(description = "Novo abatimento comercial fixo em BRL por tonelada", example = "12.50")
        @NotNull(message = "O ajuste comercial por tonelada é obrigatório")
        @DecimalMin(value = "0.00", inclusive = true, message = "O ajuste comercial por tonelada não pode ser negativo")
        BigDecimal adjustmentPerTon
) {
}
