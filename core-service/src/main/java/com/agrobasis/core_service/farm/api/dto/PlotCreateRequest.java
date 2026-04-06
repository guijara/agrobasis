package com.agrobasis.core_service.farm.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record PlotCreateRequest(
        @Schema(description = "Nome ou número do talhão", example = "Talhão 01")
        @NotBlank(message = "O nome do talhão é obrigatório")
        String name,

        @Schema(description = "Área produtiva do talhão em hectares", example = "50.5")
        @NotNull(message = "A área em hectares é obrigatória")
        @Positive(message = "A área deve ser maior que zero")
        Double hectareArea,

        @Schema(description = "ID da fazenda vinculada")
        @NotNull(message = "O ID da fazenda é obrigatório")
        UUID farmId
) {
}
