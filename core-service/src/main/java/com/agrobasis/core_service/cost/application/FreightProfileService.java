package com.agrobasis.core_service.cost.application;

import com.agrobasis.core_service.cost.api.dto.FreightProfileCreateRequest;
import com.agrobasis.core_service.cost.api.dto.FreightProfileResponse;
import com.agrobasis.core_service.cost.api.dto.FreightProfileUpdateRequest;
import com.agrobasis.core_service.cost.domain.FreightProfile;
import com.agrobasis.core_service.cost.domain.exception.FreightProfileAlreadyExistsException;
import com.agrobasis.core_service.cost.domain.exception.FreightProfileNotFoundException;
import com.agrobasis.core_service.cost.infrastructure.FreightProfileRepository;
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
public class FreightProfileService {

    private final FreightProfileRepository freightProfileRepository;
    private final OrganizationRepository organizationRepository;
    private final FarmRepository farmRepository;

    public FreightProfileResponse createFreightProfile(FreightProfileCreateRequest request) {
        Organization organization = organizationRepository.findById(request.organizationId())
                .orElseThrow(() -> new OrganizationNotFoundException("Organização não encontrada."));
        Farm farm = farmRepository.findById(request.farmId())
                .orElseThrow(() -> new FarmNotFoundException("Fazenda não encontrada."));

        validateFarmBelongsToOrganization(farm, request.organizationId());

        if (freightProfileRepository.existsByOrganization_IdAndFarm_IdAndCommodity(request.organizationId(), request.farmId(), request.commodity())) {
            throw new FreightProfileAlreadyExistsException("Perfil de frete já cadastrado para a organização, fazenda e commodity informadas.");
        }

        FreightProfile freightProfile = new FreightProfile();
        freightProfile.setOrganization(organization);
        freightProfile.setFarm(farm);
        freightProfile.setCommodity(request.commodity());
        freightProfile.setFreightPerTon(request.freightPerTon());

        FreightProfile savedFreightProfile = freightProfileRepository.save(freightProfile);
        return toResponse(savedFreightProfile);
    }

    public FreightProfileResponse getFreightProfileById(UUID id, UUID organizationId) {
        FreightProfile freightProfile = freightProfileRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new FreightProfileNotFoundException("Perfil de frete não encontrado."));

        return toResponse(freightProfile);
    }

    public FreightProfileResponse getFreightProfileByFarmAndCommodity(UUID organizationId, UUID farmId, Commodity commodity) {
        FreightProfile freightProfile = freightProfileRepository.findByOrganization_IdAndFarm_IdAndCommodity(organizationId, farmId, commodity)
                .orElseThrow(() -> new FreightProfileNotFoundException("Perfil de frete não encontrado."));

        return toResponse(freightProfile);
    }

    public List<FreightProfileResponse> listFreightProfilesByOrganization(UUID organizationId) {
        return freightProfileRepository.findAllByOrganization_Id(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<FreightProfileResponse> listFreightProfilesByOrganizationAndFarm(UUID organizationId, UUID farmId) {
        return freightProfileRepository.findAllByOrganization_IdAndFarm_Id(organizationId, farmId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public FreightProfileResponse updateFreightProfile(UUID id, UUID organizationId, FreightProfileUpdateRequest request) {
        FreightProfile freightProfile = freightProfileRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new FreightProfileNotFoundException("Perfil de frete não encontrado."));

        freightProfile.setFreightPerTon(request.freightPerTon());
        freightProfileRepository.save(freightProfile);

        return toResponse(freightProfile);
    }

    private void validateFarmBelongsToOrganization(Farm farm, UUID organizationId) {
        if (!farm.getOrganization().getId().equals(organizationId)) {
            throw new TenantAccessDeniedException("A fazenda informada não pertence à organização informada.");
        }
    }

    private FreightProfileResponse toResponse(FreightProfile freightProfile) {
        return new FreightProfileResponse(
                freightProfile.getId(),
                freightProfile.getOrganization().getId(),
                freightProfile.getFarm().getId(),
                freightProfile.getCommodity(),
                freightProfile.getFreightPerTon(),
                freightProfile.getCreatedAt(),
                freightProfile.getUpdatedAt()
        );
    }
}
