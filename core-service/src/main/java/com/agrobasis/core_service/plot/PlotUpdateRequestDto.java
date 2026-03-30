package com.agrobasis.core_service.plot;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PlotUpdateRequestDto(
        @Schema(description = "Novo nome do talhão", example = "Talhão 01")
        @NotBlank(message = "O nome do talhão é obrigatório")
        String name,

        @Schema(description = "Nova área do talhão", example = "55.0")
        @NotNull(message = "A área em hectares é obrigatória")
        @Positive(message = "A área deve ser maior que zero")
        Double hectareArea
) {
}
