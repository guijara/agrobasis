package com.agrobasis.core_service.plot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PlotUpdateRequestDto(
        @NotBlank(message = "O nome do talhão é obrigatório")
        String name,
        @NotNull(message = "A área em hectares é obrigatória")
        @Positive(message = "A área deve ser maior que zero")
        Double hectareArea
) {
}
