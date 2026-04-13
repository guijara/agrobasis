package com.agrobasis.core_service.pricing.api;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.identity.infrastructure.security.AuthenticatedUser;
import com.agrobasis.core_service.pricing.api.dto.CurrentPricingResponse;
import com.agrobasis.core_service.pricing.application.PricingService;
import com.agrobasis.core_service.shared.api.doc.ApiStandardErrors;
import com.agrobasis.core_service.shared.security.TenantAccessValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
@Tag(name = "Pricing", description = "Endpoints para cálculo de preço atual de commodities")
@ApiStandardErrors
public class PricingController {

    private final PricingService pricingService;
    private final TenantAccessValidator tenantAccessValidator;

    @Operation(summary = "Calcula o preço atual líquido", description = "Busca a última cotação da commodity, a última taxa USD para BRL, o perfil de custo e o perfil de frete para calcular o preço líquido preliminar em BRL por tonelada.")
    @ApiResponse(responseCode = "200", description = "Preço calculado com sucesso")
    @GetMapping("/current")
    public ResponseEntity<CurrentPricingResponse> getCurrentPricing(
            @RequestParam UUID organizationId,
            @RequestParam UUID farmId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam Commodity commodity
    ) {
        tenantAccessValidator.assertOrganizationAccess(authenticatedUser, organizationId);
        CurrentPricingResponse response = pricingService.calculateCurrentPrice(organizationId, farmId, commodity);
        return ResponseEntity.ok(response);
    }
}
