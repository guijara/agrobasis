package com.agrobasis.core_service.farm.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record FarmCreateRequest(
        @Schema(description = "Nome da fazenda", example = "Fazenda")
        @NotBlank(message = "O nome da fazenda é obrigatório")
        String name,

        @Schema(description = "Localização geográfica ou cidade", example = "Cuiabá")
        @NotBlank(message = "A localização da fazenda é obrigatória")
        String location,

        @Schema(description = "Área total da fazenda em hectares", example = "1500.50")
        @NotNull(message = "A área em hectares é obrigatória")
        @Positive(message = "A área deve ser maior que zero")
        Double hectareArea,

        @Schema(description = "ID da organização à qual a fazenda pertence")
        @NotNull(message = "O ID da organização é obrigatório")
        UUID organizationId
) {
}
