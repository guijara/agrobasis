package com.agrobasis.core_service.market.api;

import com.agrobasis.core_service.market.api.dto.ExchangeRateCreateRequest;
import com.agrobasis.core_service.market.api.dto.ExchangeRateResponse;
import com.agrobasis.core_service.market.api.dto.ExchangeRateUpdateRequest;
import com.agrobasis.core_service.market.application.ExchangeRateService;
import com.agrobasis.core_service.shared.api.doc.ApiStandardErrors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/market/exchange-rates")
@RequiredArgsConstructor
@Tag(name = "Exchange Rate", description = "Endpoints para gestão de taxas de câmbio")
@ApiStandardErrors
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @Operation(summary = "Cria uma taxa de câmbio", description = "Registra uma nova taxa de câmbio.")
    @ApiResponse(responseCode = "201", description = "Taxa criada com sucesso")
    @PostMapping
    public ResponseEntity<ExchangeRateResponse> postExchangeRate(@Valid @RequestBody ExchangeRateCreateRequest request) {
        ExchangeRateResponse response = exchangeRateService.createExchangeRate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Busca taxa por ID", description = "Retorna os detalhes de uma taxa específica.")
    @ApiResponse(responseCode = "200", description = "Taxa encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<ExchangeRateResponse> getExchangeRate(@PathVariable UUID id) {
        ExchangeRateResponse response = exchangeRateService.getExchangeRateById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lista taxas de câmbio", description = "Retorna uma página com as taxas cadastradas.")
    @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    @GetMapping
    public ResponseEntity<Page<ExchangeRateResponse>> listExchangeRates(
            @ParameterObject @PageableDefault(size = 10, sort = "quotedAt") Pageable pageable) {
        Page<ExchangeRateResponse> page = exchangeRateService.getAllExchangeRates(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Atualiza uma taxa de câmbio", description = "Altera os dados de uma taxa de câmbio existente.")
    @ApiResponse(responseCode = "200", description = "Taxa atualizada com sucesso")
    @PutMapping("/{id}")
    public ResponseEntity<ExchangeRateResponse> putExchangeRate(
            @PathVariable UUID id,
            @Valid @RequestBody ExchangeRateUpdateRequest request) {
        ExchangeRateResponse response = exchangeRateService.updateExchangeRate(id, request);
        return ResponseEntity.ok(response);
    }
}
