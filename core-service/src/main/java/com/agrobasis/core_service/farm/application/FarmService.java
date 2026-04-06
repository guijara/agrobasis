package com.agrobasis.core_service.farm.application;

import com.agrobasis.core_service.farm.api.dto.FarmCreateRequest;
import com.agrobasis.core_service.farm.api.dto.FarmResponse;
import com.agrobasis.core_service.farm.api.dto.FarmUpdateRequest;
import com.agrobasis.core_service.farm.domain.Farm;
import com.agrobasis.core_service.farm.domain.exception.FarmNotFoundException;
import com.agrobasis.core_service.farm.infrastructure.FarmRepository;
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.domain.exception.OrganizationNotFoundException;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FarmService {

    private final FarmRepository farmRepository;
    private final OrganizationRepository organizationRepository;

    public FarmResponse createFarm(FarmCreateRequest request){

        Organization organization = organizationRepository.
                findById(request.organizationId()).orElseThrow(() ->
                        new OrganizationNotFoundException("Organização não encontrada."));

        Farm farm = new Farm();
        farm.setName(request.name());
        farm.setLocation(request.location());
        farm.setOrganization(organization);
        farm.setHectareArea(request.hectareArea());

        Farm savedFarm = farmRepository.save(farm);

        return new FarmResponse(
                savedFarm.getId(),
                savedFarm.getName(),
                savedFarm.getLocation(),
                savedFarm.getHectareArea(),
                savedFarm.getOrganization().getId()
        );
    }

    public FarmResponse getFarmById(UUID id) {

        Farm farm = farmRepository.findById(id).orElseThrow(() -> new FarmNotFoundException("Fazenda não encontrada."));

        return new FarmResponse(
                farm.getId(),
                farm.getName(),
                farm.getLocation(),
                farm.getHectareArea(),
                farm.getOrganization().getId()
        );
    }

    public Page<FarmResponse> getAllFarmsByOrganization(UUID organizationId, Pageable pageable) {

        Page<Farm> farms = farmRepository.findAllByOrganizationId(organizationId,pageable);

        return farms.map(Farm -> new FarmResponse(
                Farm.getId(),
                Farm.getName(),
                Farm.getLocation(),
                Farm.getHectareArea(),
                Farm.getOrganization().getId()
        ));
    }

    @Transactional
    public FarmResponse updateFarm(UUID id, FarmUpdateRequest request){
        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new FarmNotFoundException("Fazenda não encontrada."));

        farm.setName(request.name());
        farm.setLocation(request.location());
        farm.setHectareArea(request.hectareArea());

        farmRepository.save(farm);

        return new FarmResponse(
                farm.getId(),
                farm.getName(),
                farm.getLocation(),
                farm.getHectareArea(),
                farm.getOrganization().getId()
        );
    }
}
