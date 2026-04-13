package com.agrobasis.core_service.cost.api;

import com.agrobasis.core_service.cost.api.dto.FreightProfileCreateRequest;
import com.agrobasis.core_service.cost.api.dto.FreightProfileResponse;
import com.agrobasis.core_service.cost.api.dto.FreightProfileUpdateRequest;
import com.agrobasis.core_service.cost.application.FreightProfileService;
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
@RequestMapping("/api/cost/freight-profiles")
@RequiredArgsConstructor
@Tag(name = "Freight Profile", description = "Endpoints para gestão de frete por organização, fazenda e commodity")
@ApiStandardErrors
public class FreightProfileController {

    private final FreightProfileService freightProfileService;
    private final TenantAccessValidator tenantAccessValidator;

    @Operation(summary = "Cria um perfil de frete", description = "Registra o frete base em BRL por tonelada para uma organização, fazenda e commodity.")
    @ApiResponse(responseCode = "201", description = "Perfil de frete criado com sucesso")
    @PostMapping
    public ResponseEntity<FreightProfileResponse> postFreightProfile(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody FreightProfileCreateRequest request
    ) {
        tenantAccessValidator.assertOrganizationAccess(authenticatedUser, request.organizationId());
        FreightProfileResponse response = freightProfileService.createFreightProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Busca perfil de frete por ID", description = "Retorna um perfil de frete específico.")
    @ApiResponse(responseCode = "200", description = "Perfil de frete encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<FreightProfileResponse> getFreightProfile(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        FreightProfileResponse response = freightProfileService.getFreightProfileById(id, authenticatedUser.organizationId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Busca perfil por fazenda e commodity", description = "Retorna o perfil de frete para a organização, fazenda e commodity informadas.")
    @ApiResponse(responseCode = "200", description = "Perfil de frete encontrado")
    @GetMapping("/search")
    public ResponseEntity<FreightProfileResponse> searchFreightProfile(
            @RequestParam UUID organizationId,
            @RequestParam UUID farmId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam Commodity commodity
    ) {
        tenantAccessValidator.assertOrganizationAccess(authenticatedUser, organizationId);
        FreightProfileResponse response = freightProfileService.getFreightProfileByFarmAndCommodity(organizationId, farmId, commodity);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lista perfis de frete por organização", description = "Retorna todos os perfis de frete cadastrados para a organização informada.")
    @ApiResponse(responseCode = "200", description = "Perfis de frete listados com sucesso")
    @GetMapping
    public ResponseEntity<List<FreightProfileResponse>> listFreightProfiles(
            @RequestParam UUID organizationId,
            @RequestParam(required = false) UUID farmId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        tenantAccessValidator.assertOrganizationAccess(authenticatedUser, organizationId);
        List<FreightProfileResponse> response = farmId == null
                ? freightProfileService.listFreightProfilesByOrganization(organizationId)
                : freightProfileService.listFreightProfilesByOrganizationAndFarm(organizationId, farmId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualiza um perfil de frete", description = "Atualiza o frete base por tonelada de um perfil existente.")
    @ApiResponse(responseCode = "200", description = "Perfil de frete atualizado com sucesso")
    @PutMapping("/{id}")
    public ResponseEntity<FreightProfileResponse> putFreightProfile(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody FreightProfileUpdateRequest request
    ) {
        FreightProfileResponse response = freightProfileService.updateFreightProfile(id, authenticatedUser.organizationId(), request);
        return ResponseEntity.ok(response);
    }
}
