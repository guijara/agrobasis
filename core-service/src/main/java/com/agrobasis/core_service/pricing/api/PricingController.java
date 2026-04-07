package com.agrobasis.core_service.pricing.api;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.pricing.api.dto.CurrentPricingResponse;
import com.agrobasis.core_service.pricing.application.PricingService;
import com.agrobasis.core_service.shared.api.doc.ApiStandardErrors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
@Tag(name = "Pricing", description = "Endpoints para cálculo de preço atual de commodities")
@ApiStandardErrors
public class PricingController {

    private final PricingService pricingService;

    @Operation(summary = "Calcula o preço atual convertido", description = "Busca a última cotação da commodity e a última taxa USD para BRL para calcular o preço atual em BRL por tonelada.")
    @ApiResponse(responseCode = "200", description = "Preço calculado com sucesso")
    @GetMapping("/current")
    public ResponseEntity<CurrentPricingResponse> getCurrentPricing(@RequestParam Commodity commodity) {
        CurrentPricingResponse response = pricingService.calculateCurrentPrice(commodity);
        return ResponseEntity.ok(response);
    }
}
