package com.agrobasis.core_service.farm.infrastructure;

import com.agrobasis.core_service.farm.domain.Farm;
import com.agrobasis.core_service.farm.domain.Plot;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PlotRepositoryIT {

    @Autowired
    private PlotRepository plotRepository;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    @DisplayName("Should save plot successfully")
    void shouldSavePlotSuccessfully() {
        Organization organization = createOrganization("AgroTech", "22.222.222/0001-22");
        Farm farm = createFarm("Fazenda Boa Terra", "Cuiaba", 1200.0, organization);

        Plot plot = new Plot();
        plot.setName("Talhao 01");
        plot.setHectareArea(150.0);
        plot.setFarm(farm);

        Plot savedPlot = plotRepository.save(plot);

        assertThat(savedPlot.getId()).isNotNull();
        assertThat(savedPlot.getName()).isEqualTo("Talhao 01");
        assertThat(savedPlot.getHectareArea()).isEqualTo(150.0);
        assertThat(savedPlot.getFarm().getId()).isEqualTo(farm.getId());
    }

    @Test
    @DisplayName("Should find plots by farm with pagination")
    void shouldFindPlotsByFarmWithPagination() {
        Organization organization = createOrganization("AgroTech", "22.222.222/0001-22");
        Farm targetFarm = createFarm("Fazenda Boa Terra", "Cuiaba", 1200.0, organization);
        Farm otherFarm = createFarm("Fazenda Horizonte", "Sinop", 980.0, organization);

        plotRepository.save(createPlot("Talhao Norte", 120.0, targetFarm));
        plotRepository.save(createPlot("Talhao Sul", 130.0, targetFarm));
        plotRepository.save(createPlot("Talhao Externo", 140.0, otherFarm));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Plot> result = plotRepository.findAllByFarm_Id(targetFarm.getId(), pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Plot::getName)
                .containsExactlyInAnyOrder("Talhao Norte", "Talhao Sul");
        assertThat(result.getContent())
                .extracting(plot -> plot.getFarm().getId())
                .containsOnly(targetFarm.getId());
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
        return farmRepository.save(farm);
    }

    private Plot createPlot(String name, Double hectareArea, Farm farm) {
        Plot plot = new Plot();
        plot.setName(name);
        plot.setHectareArea(hectareArea);
        plot.setFarm(farm);
        return plot;
    }
}
