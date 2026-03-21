package com.agrobasis.core_service.config;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(LocalDateTime timestamp,
                            Integer status,
                            String error,
                            String message,
                            String path,
                            List<FieldErrorDetail> details
) {
    public record FieldErrorDetail(String field, String issue) {}
}
