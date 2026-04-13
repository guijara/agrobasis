package com.agrobasis.core_service.cost.api.dto;

import com.agrobasis.core_service.farm.domain.Commodity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record FreightProfileCreateRequest(
        @Schema(description = "ID da organização", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull(message = "O ID da organização é obrigatório")
        UUID organizationId,

        @Schema(description = "ID da fazenda", example = "123e4567-e89b-12d3-a456-426614174001")
        @NotNull(message = "O ID da fazenda é obrigatório")
        UUID farmId,

        @Schema(description = "Commodity do perfil de frete", example = "SOYBEAN")
        @NotNull(message = "A commodity é obrigatória")
        Commodity commodity,

        @Schema(description = "Frete base em BRL por tonelada", example = "20.00")
        @NotNull(message = "O frete por tonelada é obrigatório")
        @DecimalMin(value = "0.00", inclusive = true, message = "O frete por tonelada não pode ser negativo")
        BigDecimal freightPerTon
) {
}
