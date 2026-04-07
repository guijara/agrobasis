package com.agrobasis.core_service.farm.api.dto;

import com.agrobasis.core_service.farm.domain.Commodity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PlotUpdateRequest(
        @Schema(description = "Novo nome do talhão", example = "Talhão 01")
        @NotBlank(message = "O nome do talhão é obrigatório")
        String name,

        @Schema(description = "Nova área do talhão", example = "55.0")
        @NotNull(message = "A área em hectares é obrigatória")
        @Positive(message = "A área deve ser maior que zero")
        Double hectareArea,

        @Schema(description = "Nova commodity do talhão", example = "CORN")
        @NotNull(message = "A commodity é obrigatória")
        Commodity commodity
) {
}
