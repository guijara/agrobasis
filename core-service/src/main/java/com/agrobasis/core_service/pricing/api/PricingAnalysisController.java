package com.agrobasis.core_service.pricing.api;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.identity.infrastructure.security.AuthenticatedUser;
import com.agrobasis.core_service.pricing.api.dto.CurrentPricingAnalysisResponse;
import com.agrobasis.core_service.pricing.application.PricingAnalysisService;
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
@RequestMapping("/api/pricing/analysis")
@RequiredArgsConstructor
@Tag(name = "Pricing Analysis", description = "Endpoints para análise do pricing atual de commodities")
@ApiStandardErrors
public class PricingAnalysisController {

    private final PricingAnalysisService pricingAnalysisService;
    private final TenantAccessValidator tenantAccessValidator;

    @Operation(summary = "Analisa o pricing atual", description = "Deriva composição, impactos e indicadores analíticos a partir do cálculo atual de pricing.")
    @ApiResponse(responseCode = "200", description = "Análise calculada com sucesso")
    @GetMapping("/current")
    public ResponseEntity<CurrentPricingAnalysisResponse> getCurrentPricingAnalysis(
            @RequestParam UUID organizationId,
            @RequestParam UUID farmId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam Commodity commodity
    ) {
        tenantAccessValidator.assertOrganizationAccess(authenticatedUser, organizationId);
        CurrentPricingAnalysisResponse response = pricingAnalysisService.analyzeCurrentPricing(organizationId, farmId, commodity);
        return ResponseEntity.ok(response);
    }
}
