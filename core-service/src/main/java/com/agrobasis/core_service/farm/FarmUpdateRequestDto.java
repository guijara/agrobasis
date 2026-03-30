package com.agrobasis.core_service.farm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record FarmUpdateRequestDto(
        @Schema(description = "Novo nome da fazenda", example = "Fazenda")
        @NotBlank(message = "O nome da fazenda é obrigatório")
        String name,

        @Schema(description = "Nova localização", example = "Cuiabá")
        @NotBlank(message = "A localização da fazenda é obrigatória")
        String location,

        @Schema(description = "Nova área total em hectares", example = "2000.00")
        @NotNull(message = "A área em hectares é obrigatória")
        @Positive(message = "A área deve ser maior que zero")
        Double hectareArea
) {
}
