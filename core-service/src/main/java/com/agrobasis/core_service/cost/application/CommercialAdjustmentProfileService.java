package com.agrobasis.core_service.cost.application;

import com.agrobasis.core_service.cost.api.dto.CommercialAdjustmentProfileCreateRequest;
import com.agrobasis.core_service.cost.api.dto.CommercialAdjustmentProfileResponse;
import com.agrobasis.core_service.cost.api.dto.CommercialAdjustmentProfileUpdateRequest;
import com.agrobasis.core_service.cost.domain.CommercialAdjustmentProfile;
import com.agrobasis.core_service.cost.domain.exception.CommercialAdjustmentProfileAlreadyExistsException;
import com.agrobasis.core_service.cost.domain.exception.CommercialAdjustmentProfileNotFoundException;
import com.agrobasis.core_service.cost.infrastructure.CommercialAdjustmentProfileRepository;
import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.farm.domain.Farm;
import com.agrobasis.core_service.farm.domain.exception.FarmNotFoundException;
import com.agrobasis.core_service.farm.infrastructure.FarmRepository;
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.domain.exception.OrganizationNotFoundException;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import com.agrobasis.core_service.shared.domain.exception.TenantAccessDeniedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommercialAdjustmentProfileService {

    private final CommercialAdjustmentProfileRepository commercialAdjustmentProfileRepository;
    private final OrganizationRepository organizationRepository;
    private final FarmRepository farmRepository;

    public CommercialAdjustmentProfileResponse createCommercialAdjustmentProfile(CommercialAdjustmentProfileCreateRequest request) {
        Organization organization = organizationRepository.findById(request.organizationId())
                .orElseThrow(() -> new OrganizationNotFoundException("Organização não encontrada."));
        Farm farm = farmRepository.findById(request.farmId())
                .orElseThrow(() -> new FarmNotFoundException("Fazenda não encontrada."));

        validateFarmBelongsToOrganization(farm, request.organizationId());

        if (commercialAdjustmentProfileRepository.existsByOrganization_IdAndFarm_IdAndCommodity(request.organizationId(), request.farmId(), request.commodity())) {
            throw new CommercialAdjustmentProfileAlreadyExistsException("Perfil de ajuste comercial já cadastrado para a organização, fazenda e commodity informadas.");
        }

        CommercialAdjustmentProfile commercialAdjustmentProfile = new CommercialAdjustmentProfile();
        commercialAdjustmentProfile.setOrganization(organization);
        commercialAdjustmentProfile.setFarm(farm);
        commercialAdjustmentProfile.setCommodity(request.commodity());
        commercialAdjustmentProfile.setAdjustmentPerTon(request.adjustmentPerTon());

        CommercialAdjustmentProfile savedCommercialAdjustmentProfile = commercialAdjustmentProfileRepository.save(commercialAdjustmentProfile);
        return toResponse(savedCommercialAdjustmentProfile);
    }

    public CommercialAdjustmentProfileResponse getCommercialAdjustmentProfileById(UUID id, UUID organizationId) {
        CommercialAdjustmentProfile commercialAdjustmentProfile = commercialAdjustmentProfileRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new CommercialAdjustmentProfileNotFoundException("Perfil de ajuste comercial não encontrado."));

        return toResponse(commercialAdjustmentProfile);
    }

    public CommercialAdjustmentProfileResponse getCommercialAdjustmentProfileByFarmAndCommodity(UUID organizationId, UUID farmId, Commodity commodity) {
        CommercialAdjustmentProfile commercialAdjustmentProfile = commercialAdjustmentProfileRepository.findByOrganization_IdAndFarm_IdAndCommodity(organizationId, farmId, commodity)
                .orElseThrow(() -> new CommercialAdjustmentProfileNotFoundException("Perfil de ajuste comercial não encontrado."));

        return toResponse(commercialAdjustmentProfile);
    }

    public List<CommercialAdjustmentProfileResponse> listCommercialAdjustmentProfilesByOrganization(UUID organizationId) {
        return commercialAdjustmentProfileRepository.findAllByOrganization_Id(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CommercialAdjustmentProfileResponse> listCommercialAdjustmentProfilesByOrganizationAndFarm(UUID organizationId, UUID farmId) {
        return commercialAdjustmentProfileRepository.findAllByOrganization_IdAndFarm_Id(organizationId, farmId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CommercialAdjustmentProfileResponse updateCommercialAdjustmentProfile(UUID id, UUID organizationId, CommercialAdjustmentProfileUpdateRequest request) {
        CommercialAdjustmentProfile commercialAdjustmentProfile = commercialAdjustmentProfileRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new CommercialAdjustmentProfileNotFoundException("Perfil de ajuste comercial não encontrado."));

        commercialAdjustmentProfile.setAdjustmentPerTon(request.adjustmentPerTon());
        commercialAdjustmentProfileRepository.save(commercialAdjustmentProfile);

        return toResponse(commercialAdjustmentProfile);
    }

    private void validateFarmBelongsToOrganization(Farm farm, UUID organizationId) {
        if (!farm.getOrganization().getId().equals(organizationId)) {
            throw new TenantAccessDeniedException("A fazenda informada não pertence à organização informada.");
        }
    }

    private CommercialAdjustmentProfileResponse toResponse(CommercialAdjustmentProfile commercialAdjustmentProfile) {
        return new CommercialAdjustmentProfileResponse(
                commercialAdjustmentProfile.getId(),
                commercialAdjustmentProfile.getOrganization().getId(),
                commercialAdjustmentProfile.getFarm().getId(),
                commercialAdjustmentProfile.getCommodity(),
                commercialAdjustmentProfile.getAdjustmentPerTon(),
                commercialAdjustmentProfile.getCreatedAt(),
                commercialAdjustmentProfile.getUpdatedAt()
        );
    }
}
