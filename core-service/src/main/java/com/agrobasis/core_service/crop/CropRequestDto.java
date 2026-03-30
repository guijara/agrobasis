package com.agrobasis.core_service.crop;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CropRequestDto(@Schema(description = "Nome identificador da safra",
         example = "Safra Verão 2026")
         @NotBlank(message = "O nome da safra é obrigatório")
         String name,

         @Schema(description = "Produto cultivado", example = "SOJA")
         @NotBlank(message = "O produto é obrigatório")
         String product,

         @Schema(description = "Data de início do plantio")
         @NotNull(message = "A data de início é obrigatória")
         LocalDate startDate,

         @Schema(description = "Previsão de colheita")
         @NotNull(message = "A data de término é obrigatória")
         LocalDate endDate,

         @Schema(description = "ID do talhão vinculado")
         @NotNull(message = "O ID do talhão é obrigatório")
         UUID plotId
) {
}