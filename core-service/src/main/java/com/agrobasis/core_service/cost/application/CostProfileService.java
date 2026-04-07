package com.agrobasis.core_service.cost.application;

import com.agrobasis.core_service.cost.api.dto.CostProfileCreateRequest;
import com.agrobasis.core_service.cost.api.dto.CostProfileResponse;
import com.agrobasis.core_service.cost.api.dto.CostProfileUpdateRequest;
import com.agrobasis.core_service.cost.domain.CostProfile;
import com.agrobasis.core_service.cost.domain.exception.CostProfileAlreadyExistsException;
import com.agrobasis.core_service.cost.domain.exception.CostProfileNotFoundException;
import com.agrobasis.core_service.cost.infrastructure.CostProfileRepository;
import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.domain.exception.OrganizationNotFoundException;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CostProfileService {

    private final CostProfileRepository costProfileRepository;
    private final OrganizationRepository organizationRepository;

    public CostProfileResponse createCostProfile(CostProfileCreateRequest request) {
        Organization organization = organizationRepository.findById(request.organizationId())
                .orElseThrow(() -> new OrganizationNotFoundException("Organização não encontrada."));

        if (costProfileRepository.existsByOrganization_IdAndCommodity(request.organizationId(), request.commodity())) {
            throw new CostProfileAlreadyExistsException("Perfil de custo já cadastrado para a commodity informada.");
        }

        CostProfile costProfile = new CostProfile();
        costProfile.setOrganization(organization);
        costProfile.setCommodity(request.commodity());
        costProfile.setCostPerTon(request.costPerTon());

        CostProfile savedCostProfile = costProfileRepository.save(costProfile);
        return toResponse(savedCostProfile);
    }

    public CostProfileResponse getCostProfileById(UUID id) {
        CostProfile costProfile = costProfileRepository.findById(id)
                .orElseThrow(() -> new CostProfileNotFoundException("Perfil de custo não encontrado."));

        return toResponse(costProfile);
    }

    public CostProfileResponse getCostProfileByOrganizationAndCommodity(UUID organizationId, Commodity commodity) {
        CostProfile costProfile = costProfileRepository.findByOrganization_IdAndCommodity(organizationId, commodity)
                .orElseThrow(() -> new CostProfileNotFoundException("Perfil de custo não encontrado."));

        return toResponse(costProfile);
    }

    public List<CostProfileResponse> listCostProfilesByOrganization(UUID organizationId) {
        return costProfileRepository.findAllByOrganization_Id(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CostProfileResponse updateCostProfile(UUID id, CostProfileUpdateRequest request) {
        CostProfile costProfile = costProfileRepository.findById(id)
                .orElseThrow(() -> new CostProfileNotFoundException("Perfil de custo não encontrado."));

        costProfile.setCostPerTon(request.costPerTon());
        costProfileRepository.save(costProfile);

        return toResponse(costProfile);
    }

    private CostProfileResponse toResponse(CostProfile costProfile) {
        return new CostProfileResponse(
                costProfile.getId(),
                costProfile.getOrganization().getId(),
                costProfile.getCommodity(),
                costProfile.getCostPerTon(),
                costProfile.getCreatedAt(),
                costProfile.getUpdatedAt()
        );
    }
}
