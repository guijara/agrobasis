package com.agrobasis.core_service.cost.infrastructure;

import com.agrobasis.core_service.cost.domain.CommercialAdjustmentProfile;
import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.farm.domain.Farm;
import com.agrobasis.core_service.farm.infrastructure.FarmRepository;
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
class CommercialAdjustmentProfileRepositoryIT {

    @Autowired
    private CommercialAdjustmentProfileRepository commercialAdjustmentProfileRepository;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    @DisplayName("Should save commercial adjustment profile successfully")
    void shouldSaveCommercialAdjustmentProfileSuccessfully() {
        Organization organization = createOrganization("AgroTech Commercial 1", "71.111.111/0001-11");
        Farm farm = createFarm(organization, "Fazenda Comercial 1");

        CommercialAdjustmentProfile savedProfile = commercialAdjustmentProfileRepository.save(createCommercialAdjustmentProfile(organization, farm, Commodity.SOYBEAN, "10.00"));

        assertThat(savedProfile.getId()).isNotNull();
        assertThat(savedProfile.getOrganization().getId()).isEqualTo(organization.getId());
        assertThat(savedProfile.getFarm().getId()).isEqualTo(farm.getId());
        assertThat(savedProfile.getCommodity()).isEqualTo(Commodity.SOYBEAN);
        assertThat(savedProfile.getAdjustmentPerTon()).isEqualByComparingTo("10.00");
    }

    @Test
    @DisplayName("Should find commercial adjustment profile by organization, farm and commodity")
    void shouldFindCommercialAdjustmentProfileByOrganizationFarmAndCommodity() {
        Organization organization = createOrganization("AgroTech Commercial 2", "72.222.222/0001-22");
        Farm farm = createFarm(organization, "Fazenda Comercial 2");
        commercialAdjustmentProfileRepository.save(createCommercialAdjustmentProfile(organization, farm, Commodity.CORN, "15.00"));

        Optional<CommercialAdjustmentProfile> result = commercialAdjustmentProfileRepository.findByOrganization_IdAndFarm_IdAndCommodity(organization.getId(), farm.getId(), Commodity.CORN);

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getAdjustmentPerTon()).isEqualByComparingTo("15.00");
    }

    @Test
    @DisplayName("Should list commercial adjustment profiles by organization")
    void shouldListCommercialAdjustmentProfilesByOrganization() {
        Organization targetOrganization = createOrganization("AgroTech Commercial 3", "73.333.333/0001-33");
        Organization otherOrganization = createOrganization("Outra Agro Commercial", "74.444.444/0001-44");
        Farm targetFarm = createFarm(targetOrganization, "Fazenda Comercial Target");
        Farm otherFarm = createFarm(otherOrganization, "Fazenda Comercial Other");

        commercialAdjustmentProfileRepository.save(createCommercialAdjustmentProfile(targetOrganization, targetFarm, Commodity.SOYBEAN, "10.00"));
        commercialAdjustmentProfileRepository.save(createCommercialAdjustmentProfile(targetOrganization, targetFarm, Commodity.CORN, "15.00"));
        commercialAdjustmentProfileRepository.save(createCommercialAdjustmentProfile(otherOrganization, otherFarm, Commodity.SOYBEAN, "20.00"));

        List<CommercialAdjustmentProfile> result = commercialAdjustmentProfileRepository.findAllByOrganization_Id(targetOrganization.getId());

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(CommercialAdjustmentProfile::getCommodity)
                .containsExactlyInAnyOrder(Commodity.SOYBEAN, Commodity.CORN);
    }

    @Test
    @DisplayName("Should enforce uniqueness by organization, farm and commodity")
    void shouldEnforceUniquenessByOrganizationFarmAndCommodity() {
        Organization organization = createOrganization("AgroTech Commercial 4", "75.555.555/0001-55");
        Farm farm = createFarm(organization, "Fazenda Comercial Unique");

        commercialAdjustmentProfileRepository.saveAndFlush(createCommercialAdjustmentProfile(organization, farm, Commodity.SOYBEAN, "10.00"));

        assertThatThrownBy(() -> commercialAdjustmentProfileRepository.saveAndFlush(createCommercialAdjustmentProfile(organization, farm, Commodity.SOYBEAN, "12.00")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private Organization createOrganization(String name, String cnpj) {
        Organization organization = new Organization();
        organization.setName(name);
        organization.setCnpj(cnpj);
        organization.setLocation("Cuiaba");
        return organizationRepository.save(organization);
    }

    private Farm createFarm(Organization organization, String name) {
        Farm farm = new Farm();
        farm.setName(name);
        farm.setLocation("Cuiaba");
        farm.setHectareArea(1500.50);
        farm.setOrganization(organization);
        return farmRepository.save(farm);
    }

    private CommercialAdjustmentProfile createCommercialAdjustmentProfile(Organization organization, Farm farm, Commodity commodity, String adjustmentPerTon) {
        CommercialAdjustmentProfile profile = new CommercialAdjustmentProfile();
        profile.setOrganization(organization);
        profile.setFarm(farm);
        profile.setCommodity(commodity);
        profile.setAdjustmentPerTon(new BigDecimal(adjustmentPerTon));
        return profile;
    }
}
