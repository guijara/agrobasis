package com.agrobasis.core_service.farm;

import com.agrobasis.core_service.organization.Organization;
import com.agrobasis.core_service.organization.OrganizationNotFoundException;
import com.agrobasis.core_service.organization.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FarmService {

    private final FarmRepository farmRepository;
    private final OrganizationRepository organizationRepository;

    public FarmResponseDto createFarm(FarmRequestDto request){

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
}
