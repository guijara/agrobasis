package com.agrobasis.core_service.cost.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FreightProfileUpdateRequest(
        @Schema(description = "Novo frete base em BRL por tonelada", example = "22.50")
        @NotNull(message = "O frete por tonelada é obrigatório")
        @DecimalMin(value = "0.00", inclusive = true, message = "O frete por tonelada não pode ser negativo")
        BigDecimal freightPerTon
) {
}
