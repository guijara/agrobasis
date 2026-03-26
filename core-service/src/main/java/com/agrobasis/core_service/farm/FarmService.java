package com.agrobasis.core_service.farm;

import com.agrobasis.core_service.organization.Organization;
import com.agrobasis.core_service.organization.OrganizationNotFoundException;
import com.agrobasis.core_service.organization.OrganizationRepository;
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

    public FarmResponseDto createFarm(FarmCreateRequestDto request){

        Organization organization = organizationRepository.
                findById(request.organizationId()).orElseThrow(() ->
                        new OrganizationNotFoundException("Organização não encontrada."));

        Farm farm = new Farm();
        farm.setName(request.name());
        farm.setLocation(request.location());
        farm.setOrganization(organization);
        farm.setHectareArea(request.hectareArea());

        farmRepository.save(farm);

        return new FarmResponseDto(
                farm.getId(),
                farm.getName(),
                farm.getLocation(),
                farm.getHectareArea(),
                farm.getOrganization().getId()
        );
    }

    public FarmResponseDto getFarmById(UUID id) {

        Farm farm = farmRepository.findById(id).orElseThrow(() -> new FarmNotFoundException("Fazenda não encontrada."));

        return new FarmResponseDto(
                farm.getId(),
                farm.getName(),
                farm.getLocation(),
                farm.getHectareArea(),
                farm.getOrganization().getId()
        );
    }

    public Page<FarmResponseDto> getAllFarmsByOrganization(UUID organizationId, Pageable pageable) {

        Page<Farm> farms = farmRepository.findAllByOrganizationId(organizationId,pageable);

        return farms.map(Farm -> new FarmResponseDto(
                Farm.getId(),
                Farm.getName(),
                Farm.getLocation(),
                Farm.getHectareArea(),
                Farm.getOrganization().getId()
        ));
    }

    @Transactional
    public FarmResponseDto updateFarm(UUID id, FarmUpdateRequestDto request){
        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new FarmNotFoundException("Fazenda não encontrada."));

        if(!request.name().equals(farm.getName())){
            farm.setName(request.name());
        }

        if(!request.location().equals(farm.getLocation())){
            farm.setLocation(request.location());
        }

        if(!request.hectareArea().equals(farm.getHectareArea())){
            farm.setHectareArea(request.hectareArea());
        }

        farmRepository.save(farm);

        return new FarmResponseDto(
                farm.getId(),
                farm.getName(),
                farm.getLocation(),
                farm.getHectareArea(),
                farm.getOrganization().getId()
        );
    }
}
