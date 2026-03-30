package com.agrobasis.core_service.crop;

import java.time.LocalDate;
import java.util.UUID;

public record CropResponseDto(
          String name,
          String product,
          LocalDate startDate,
          LocalDate endDate,
          UUID id,
          UUID plotId) {
}
