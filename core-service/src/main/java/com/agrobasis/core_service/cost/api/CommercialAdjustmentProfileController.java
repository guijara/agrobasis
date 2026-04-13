package com.agrobasis.core_service.cost.api;

import com.agrobasis.core_service.cost.api.dto.CommercialAdjustmentProfileCreateRequest;
import com.agrobasis.core_service.cost.api.dto.CommercialAdjustmentProfileResponse;
import com.agrobasis.core_service.cost.api.dto.CommercialAdjustmentProfileUpdateRequest;
import com.agrobasis.core_service.cost.application.CommercialAdjustmentProfileService;
import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.identity.infrastructure.security.AuthenticatedUser;
import com.agrobasis.core_service.shared.api.doc.ApiStandardErrors;
import com.agrobasis.core_service.shared.security.TenantAccessValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cost/commercial-adjustment-profiles")
@RequiredArgsConstructor
@Tag(name = "Commercial Adjustment Profile", description = "Endpoints para gestão de ajuste comercial por organização, fazenda e commodity")
@ApiStandardErrors
public class CommercialAdjustmentProfileController {

    private final CommercialAdjustmentProfileService commercialAdjustmentProfileService;
    private final TenantAccessValidator tenantAccessValidator;

    @Operation(summary = "Cria um perfil de ajuste comercial", description = "Registra um abatimento comercial fixo em BRL por tonelada para uma organização, fazenda e commodity.")
    @ApiResponse(responseCode = "201", description = "Perfil de ajuste comercial criado com sucesso")
    @PostMapping
    public ResponseEntity<CommercialAdjustmentProfileResponse> postCommercialAdjustmentProfile(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody CommercialAdjustmentProfileCreateRequest request
    ) {
        tenantAccessValidator.assertOrganizationAccess(authenticatedUser, request.organizationId());
        CommercialAdjustmentProfileResponse response = commercialAdjustmentProfileService.createCommercialAdjustmentProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Busca perfil de ajuste comercial por ID", description = "Retorna um perfil de ajuste comercial específico.")
    @ApiResponse(responseCode = "200", description = "Perfil de ajuste comercial encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<CommercialAdjustmentProfileResponse> getCommercialAdjustmentProfile(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        CommercialAdjustmentProfileResponse response = commercialAdjustmentProfileService.getCommercialAdjustmentProfileById(id, authenticatedUser.organizationId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Busca perfil por fazenda e commodity", description = "Retorna o perfil de ajuste comercial para a organização, fazenda e commodity informadas.")
    @ApiResponse(responseCode = "200", description = "Perfil de ajuste comercial encontrado")
    @GetMapping("/search")
    public ResponseEntity<CommercialAdjustmentProfileResponse> searchCommercialAdjustmentProfile(
            @RequestParam UUID organizationId,
            @RequestParam UUID farmId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam Commodity commodity
    ) {
        tenantAccessValidator.assertOrganizationAccess(authenticatedUser, organizationId);
        CommercialAdjustmentProfileResponse response = commercialAdjustmentProfileService.getCommercialAdjustmentProfileByFarmAndCommodity(organizationId, farmId, commodity);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lista perfis de ajuste comercial por organização", description = "Retorna todos os perfis de ajuste comercial cadastrados para a organização informada.")
    @ApiResponse(responseCode = "200", description = "Perfis de ajuste comercial listados com sucesso")
    @GetMapping
    public ResponseEntity<List<CommercialAdjustmentProfileResponse>> listCommercialAdjustmentProfiles(
            @RequestParam UUID organizationId,
            @RequestParam(required = false) UUID farmId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        tenantAccessValidator.assertOrganizationAccess(authenticatedUser, organizationId);
        List<CommercialAdjustmentProfileResponse> response = farmId == null
                ? commercialAdjustmentProfileService.listCommercialAdjustmentProfilesByOrganization(organizationId)
                : commercialAdjustmentProfileService.listCommercialAdjustmentProfilesByOrganizationAndFarm(organizationId, farmId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualiza um perfil de ajuste comercial", description = "Atualiza o abatimento comercial fixo por tonelada de um perfil existente.")
    @ApiResponse(responseCode = "200", description = "Perfil de ajuste comercial atualizado com sucesso")
    @PutMapping("/{id}")
    public ResponseEntity<CommercialAdjustmentProfileResponse> putCommercialAdjustmentProfile(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody CommercialAdjustmentProfileUpdateRequest request
    ) {
        CommercialAdjustmentProfileResponse response = commercialAdjustmentProfileService.updateCommercialAdjustmentProfile(id, authenticatedUser.organizationId(), request);
        return ResponseEntity.ok(response);
    }
}
