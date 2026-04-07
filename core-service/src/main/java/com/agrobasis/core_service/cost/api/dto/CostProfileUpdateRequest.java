package com.agrobasis.core_service.cost.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CostProfileUpdateRequest(
        @Schema(description = "Novo custo base em BRL por tonelada", example = "50.00")
        @NotNull(message = "O custo por tonelada é obrigatório")
        @DecimalMin(value = "0.00", inclusive = true, message = "O custo por tonelada não pode ser negativo")
        BigDecimal costPerTon
) {
}
