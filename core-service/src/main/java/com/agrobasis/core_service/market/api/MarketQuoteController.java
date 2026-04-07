package com.agrobasis.core_service.market.api;

import com.agrobasis.core_service.market.api.dto.MarketQuoteCreateRequest;
import com.agrobasis.core_service.market.api.dto.MarketQuoteResponse;
import com.agrobasis.core_service.market.api.dto.MarketQuoteUpdateRequest;
import com.agrobasis.core_service.market.application.MarketQuoteService;
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
@RequestMapping("/api/market/quotes")
@RequiredArgsConstructor
@Tag(name = "Market Quote", description = "Endpoints para gestão de cotações de mercado")
@ApiStandardErrors
public class MarketQuoteController {

    private final MarketQuoteService marketQuoteService;

    @Operation(summary = "Cria uma cotação de mercado", description = "Registra uma nova cotação para uma commodity.")
    @ApiResponse(responseCode = "201", description = "Cotação criada com sucesso")
    @PostMapping
    public ResponseEntity<MarketQuoteResponse> postMarketQuote(@Valid @RequestBody MarketQuoteCreateRequest request) {
        MarketQuoteResponse response = marketQuoteService.createMarketQuote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Busca cotação por ID", description = "Retorna os detalhes de uma cotação específica.")
    @ApiResponse(responseCode = "200", description = "Cotação encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<MarketQuoteResponse> getMarketQuote(@PathVariable UUID id) {
        MarketQuoteResponse response = marketQuoteService.getMarketQuoteById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lista cotações de mercado", description = "Retorna uma página com as cotações cadastradas.")
    @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    @GetMapping
    public ResponseEntity<Page<MarketQuoteResponse>> listMarketQuotes(
            @ParameterObject @PageableDefault(size = 10, sort = "quotedAt") Pageable pageable) {
        Page<MarketQuoteResponse> page = marketQuoteService.getAllMarketQuotes(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Atualiza uma cotação de mercado", description = "Altera os dados de uma cotação existente.")
    @ApiResponse(responseCode = "200", description = "Cotação atualizada com sucesso")
    @PutMapping("/{id}")
    public ResponseEntity<MarketQuoteResponse> putMarketQuote(
            @PathVariable UUID id,
            @Valid @RequestBody MarketQuoteUpdateRequest request) {
        MarketQuoteResponse response = marketQuoteService.updateMarketQuote(id, request);
        return ResponseEntity.ok(response);
    }
}
