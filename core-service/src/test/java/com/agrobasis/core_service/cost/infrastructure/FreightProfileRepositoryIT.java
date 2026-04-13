package com.agrobasis.core_service.cost.infrastructure;

import com.agrobasis.core_service.cost.domain.FreightProfile;
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
class FreightProfileRepositoryIT {

    @Autowired
    private FreightProfileRepository freightProfileRepository;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    @DisplayName("Should save freight profile successfully")
    void shouldSaveFreightProfileSuccessfully() {
        Organization organization = createOrganization("AgroTech Freight", "11.111.111/0001-11");
        Farm farm = createFarm(organization, "Fazenda Frete 1");

        FreightProfile savedFreightProfile = freightProfileRepository.save(createFreightProfile(organization, farm, Commodity.SOYBEAN, "20.00"));

        assertThat(savedFreightProfile.getId()).isNotNull();
        assertThat(savedFreightProfile.getOrganization().getId()).isEqualTo(organization.getId());
        assertThat(savedFreightProfile.getFarm().getId()).isEqualTo(farm.getId());
        assertThat(savedFreightProfile.getCommodity()).isEqualTo(Commodity.SOYBEAN);
        assertThat(savedFreightProfile.getFreightPerTon()).isEqualByComparingTo("20.00");
    }

    @Test
    @DisplayName("Should find freight profile by organization, farm and commodity")
    void shouldFindFreightProfileByOrganizationFarmAndCommodity() {
        Organization organization = createOrganization("AgroTech Freight 2", "22.222.222/0001-22");
        Farm farm = createFarm(organization, "Fazenda Frete 2");
        freightProfileRepository.save(createFreightProfile(organization, farm, Commodity.CORN, "30.00"));

        Optional<FreightProfile> result = freightProfileRepository.findByOrganization_IdAndFarm_IdAndCommodity(organization.getId(), farm.getId(), Commodity.CORN);

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getFreightPerTon()).isEqualByComparingTo("30.00");
    }

    @Test
    @DisplayName("Should list freight profiles by organization")
    void shouldListFreightProfilesByOrganization() {
        Organization targetOrganization = createOrganization("AgroTech Freight 3", "33.333.333/0001-33");
        Organization otherOrganization = createOrganization("Outra Agro Freight", "44.444.444/0001-44");
        Farm targetFarm = createFarm(targetOrganization, "Fazenda Target");
        Farm otherFarm = createFarm(otherOrganization, "Fazenda Other");

        freightProfileRepository.save(createFreightProfile(targetOrganization, targetFarm, Commodity.SOYBEAN, "20.00"));
        freightProfileRepository.save(createFreightProfile(targetOrganization, targetFarm, Commodity.CORN, "30.00"));
        freightProfileRepository.save(createFreightProfile(otherOrganization, otherFarm, Commodity.SOYBEAN, "40.00"));

        List<FreightProfile> result = freightProfileRepository.findAllByOrganization_Id(targetOrganization.getId());

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(FreightProfile::getCommodity)
                .containsExactlyInAnyOrder(Commodity.SOYBEAN, Commodity.CORN);
    }

    @Test
    @DisplayName("Should enforce uniqueness by organization, farm and commodity")
    void shouldEnforceUniquenessByOrganizationFarmAndCommodity() {
        Organization organization = createOrganization("AgroTech Freight 4", "55.555.555/0001-55");
        Farm farm = createFarm(organization, "Fazenda Unique");

        freightProfileRepository.saveAndFlush(createFreightProfile(organization, farm, Commodity.SOYBEAN, "20.00"));

        assertThatThrownBy(() -> freightProfileRepository.saveAndFlush(createFreightProfile(organization, farm, Commodity.SOYBEAN, "22.00")))
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

    private FreightProfile createFreightProfile(Organization organization, Farm farm, Commodity commodity, String freightPerTon) {
        FreightProfile freightProfile = new FreightProfile();
        freightProfile.setOrganization(organization);
        freightProfile.setFarm(farm);
        freightProfile.setCommodity(commodity);
        freightProfile.setFreightPerTon(new BigDecimal(freightPerTon));
        return freightProfile;
    }
}
