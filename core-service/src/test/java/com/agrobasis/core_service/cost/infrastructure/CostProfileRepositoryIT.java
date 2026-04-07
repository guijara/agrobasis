package com.agrobasis.core_service.cost.infrastructure;

import com.agrobasis.core_service.cost.domain.CostProfile;
import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CostProfileRepositoryIT {

    @Autowired
    private CostProfileRepository costProfileRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    @DisplayName("Should save cost profile successfully")
    void shouldSaveCostProfileSuccessfully() {
        Organization organization = createOrganization("AgroTech", "44.444.444/0001-44");

        CostProfile costProfile = createCostProfile(organization, Commodity.SOYBEAN, "45.00");

        CostProfile savedCostProfile = costProfileRepository.save(costProfile);

        assertThat(savedCostProfile.getId()).isNotNull();
        assertThat(savedCostProfile.getOrganization().getId()).isEqualTo(organization.getId());
        assertThat(savedCostProfile.getCommodity()).isEqualTo(Commodity.SOYBEAN);
        assertThat(savedCostProfile.getCostPerTon()).isEqualByComparingTo("45.00");
    }

    @Test
    @DisplayName("Should find cost profile by organization and commodity")
    void shouldFindCostProfileByOrganizationAndCommodity() {
        Organization organization = createOrganization("AgroTech", "55.555.555/0001-55");
        costProfileRepository.save(createCostProfile(organization, Commodity.CORN, "50.00"));

        Optional<CostProfile> result = costProfileRepository.findByOrganization_IdAndCommodity(organization.getId(), Commodity.CORN);

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getCostPerTon()).isEqualByComparingTo("50.00");
    }

    @Test
    @DisplayName("Should list cost profiles by organization")
    void shouldListCostProfilesByOrganization() {
        Organization targetOrganization = createOrganization("AgroTech", "66.666.666/0001-66");
        Organization otherOrganization = createOrganization("Outra Agro", "77.777.777/0001-77");

        costProfileRepository.save(createCostProfile(targetOrganization, Commodity.SOYBEAN, "45.00"));
        costProfileRepository.save(createCostProfile(targetOrganization, Commodity.CORN, "50.00"));
        costProfileRepository.save(createCostProfile(otherOrganization, Commodity.SOYBEAN, "60.00"));

        List<CostProfile> result = costProfileRepository.findAllByOrganization_Id(targetOrganization.getId());

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(CostProfile::getCommodity)
                .containsExactlyInAnyOrder(Commodity.SOYBEAN, Commodity.CORN);
    }

    @Test
    @DisplayName("Should enforce uniqueness by organization and commodity")
    void shouldEnforceUniquenessByOrganizationAndCommodity() {
        Organization organization = createOrganization("AgroTech", "88.888.888/0001-88");

        costProfileRepository.saveAndFlush(createCostProfile(organization, Commodity.SOYBEAN, "45.00"));

        assertThatThrownBy(() -> costProfileRepository.saveAndFlush(createCostProfile(organization, Commodity.SOYBEAN, "47.00")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private Organization createOrganization(String name, String cnpj) {
        Organization organization = new Organization();
        organization.setName(name);
        organization.setCnpj(cnpj);
        organization.setLocation("Cuiaba");
        return organizationRepository.save(organization);
    }

    private CostProfile createCostProfile(Organization organization, Commodity commodity, String costPerTon) {
        CostProfile costProfile = new CostProfile();
        costProfile.setOrganization(organization);
        costProfile.setCommodity(commodity);
        costProfile.setCostPerTon(new BigDecimal(costPerTon));
        return costProfile;
    }
}
