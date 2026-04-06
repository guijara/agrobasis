package com.agrobasis.core_service.farm.infrastructure;

import com.agrobasis.core_service.farm.domain.Farm;
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FarmRepositoryIT {

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    @DisplayName("Should save farm successfully")
    void shouldSaveFarmSuccessfully() {
        Organization organization = createOrganization("AgroTech", "00.000.000/0001-00");

        Farm farm = new Farm();
        farm.setName("Fazenda Boa Terra");
        farm.setLocation("Cuiaba");
        farm.setHectareArea(1200.0);
        farm.setOrganization(organization);

        Farm savedFarm = farmRepository.save(farm);

        assertThat(savedFarm.getId()).isNotNull();
        assertThat(savedFarm.getName()).isEqualTo("Fazenda Boa Terra");
        assertThat(savedFarm.getLocation()).isEqualTo("Cuiaba");
        assertThat(savedFarm.getHectareArea()).isEqualTo(1200.0);
        assertThat(savedFarm.getOrganization().getId()).isEqualTo(organization.getId());
    }

    @Test
    @DisplayName("Should find farms by organization with pagination")
    void shouldFindFarmsByOrganizationWithPagination() {
        Organization targetOrganization = createOrganization("AgroTech", "00.000.000/0001-00");
        Organization otherOrganization = createOrganization("Outra Agro", "11.111.111/0001-11");

        farmRepository.save(createFarm("Fazenda Norte", "Cuiaba", 1000.0, targetOrganization));
        farmRepository.save(createFarm("Fazenda Sul", "Rondonopolis", 850.0, targetOrganization));
        farmRepository.save(createFarm("Fazenda Externa", "Sinop", 930.0, otherOrganization));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Farm> result = farmRepository.findAllByOrganizationId(targetOrganization.getId(), pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Farm::getName)
                .containsExactlyInAnyOrder("Fazenda Norte", "Fazenda Sul");
        assertThat(result.getContent())
                .extracting(farm -> farm.getOrganization().getId())
                .containsOnly(targetOrganization.getId());
    }

    private Organization createOrganization(String name, String cnpj) {
        Organization organization = new Organization();
        organization.setName(name);
        organization.setCnpj(cnpj);
        organization.setLocation("Cuiaba");
        return organizationRepository.save(organization);
    }

    private Farm createFarm(String name, String location, Double hectareArea, Organization organization) {
        Farm farm = new Farm();
        farm.setName(name);
        farm.setLocation(location);
        farm.setHectareArea(hectareArea);
        farm.setOrganization(organization);
        return farm;
    }
}
