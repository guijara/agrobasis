package com.agrobasis.core_service.cost.api;

import com.agrobasis.core_service.cost.api.dto.CostProfileCreateRequest;
import com.agrobasis.core_service.cost.api.dto.CostProfileResponse;
import com.agrobasis.core_service.cost.api.dto.CostProfileUpdateRequest;
import com.agrobasis.core_service.cost.application.CostProfileService;
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
@RequestMapping("/api/cost/profiles")
@RequiredArgsConstructor
@Tag(name = "Cost Profile", description = "Endpoints para gestão de perfis de custo por organização")
@ApiStandardErrors
public class CostProfileController {

    private final CostProfileService costProfileService;
    private final TenantAccessValidator tenantAccessValidator;

    @Operation(summary = "Cria um perfil de custo", description = "Registra o custo base por tonelada de uma commodity para uma organização.")
    @ApiResponse(responseCode = "201", description = "Perfil de custo criado com sucesso")
    @PostMapping
    public ResponseEntity<CostProfileResponse> postCostProfile(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody CostProfileCreateRequest request
    ) {
        tenantAccessValidator.assertOrganizationAccess(authenticatedUser, request.organizationId());
        CostProfileResponse response = costProfileService.createCostProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Busca perfil de custo por ID", description = "Retorna um perfil de custo específico.")
    @ApiResponse(responseCode = "200", description = "Perfil de custo encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<CostProfileResponse> getCostProfile(@PathVariable UUID id) {
        CostProfileResponse response = costProfileService.getCostProfileById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Busca perfil por organização e commodity", description = "Retorna o perfil de custo cadastrado para a organização e commodity informadas.")
    @ApiResponse(responseCode = "200", description = "Perfil de custo encontrado")
    @GetMapping("/search")
    public ResponseEntity<CostProfileResponse> searchCostProfile(
            @RequestParam UUID organizationId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam Commodity commodity) {
        tenantAccessValidator.assertOrganizationAccess(authenticatedUser, organizationId);
        CostProfileResponse response = costProfileService.getCostProfileByOrganizationAndCommodity(organizationId, commodity);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lista perfis de custo por organização", description = "Retorna todos os perfis de custo cadastrados para a organização informada.")
    @ApiResponse(responseCode = "200", description = "Perfis de custo listados com sucesso")
    @GetMapping
    public ResponseEntity<List<CostProfileResponse>> listCostProfiles(
            @RequestParam UUID organizationId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        tenantAccessValidator.assertOrganizationAccess(authenticatedUser, organizationId);
        List<CostProfileResponse> response = costProfileService.listCostProfilesByOrganization(organizationId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualiza um perfil de custo", description = "Atualiza o custo base por tonelada de um perfil existente.")
    @ApiResponse(responseCode = "200", description = "Perfil de custo atualizado com sucesso")
    @PutMapping("/{id}")
    public ResponseEntity<CostProfileResponse> putCostProfile(
            @PathVariable UUID id,
            @Valid @RequestBody CostProfileUpdateRequest request) {
        CostProfileResponse response = costProfileService.updateCostProfile(id, request);
        return ResponseEntity.ok(response);
    }
}
