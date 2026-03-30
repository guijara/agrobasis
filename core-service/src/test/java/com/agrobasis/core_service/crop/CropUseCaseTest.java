package com.agrobasis.core_service.crop;

import com.agrobasis.core_service.farm.Farm;
import com.agrobasis.core_service.farm.FarmRepository;
import com.agrobasis.core_service.organization.Organization;
import com.agrobasis.core_service.organization.OrganizationRepository;
import com.agrobasis.core_service.plot.Plot;
import com.agrobasis.core_service.plot.PlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CropUseCaseTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private FarmRepository farmRepository;
    @Autowired
    private PlotRepository plotRepository;
    @Autowired
    private CropRepository cropRepository;

    private UUID savedPlotId;

    @BeforeEach
    void setUp() {
        cropRepository.deleteAll();
        plotRepository.deleteAll();
        farmRepository.deleteAll();
        organizationRepository.deleteAll();

        Organization org = new Organization();
        org.setName("Agro Corp");
        org.setCnpj("12.345.678/0001-90");
        org.setLocation("Cuiabá");
        org = organizationRepository.save(org);

        Farm farm = new Farm();
        farm.setName("Fazenda Sol Nascente");
        farm.setLocation("MT-251");
        farm.setHectareArea(1000.0);
        farm.setOrganization(org);
        farm = farmRepository.save(farm);

        Plot plot = new Plot();
        plot.setName("Talhão 01");
        plot.setHectareArea(50.0);
        plot.setFarm(farm);
        plot = plotRepository.save(plot);

        this.savedPlotId = plot.getId();
    }

    @Nested
    @DisplayName("POST /api/crop")
    class CreateCropUseCase {

        @Test
        @DisplayName("Should create crop successfully when data is valid")
        void shouldCreateCrop() {
            CropRequestDto request = new CropRequestDto(
                    "Soja 2026", "SOJA",
                    LocalDate.of(2026, 10, 1),
                    LocalDate.of(2027, 2, 1),
                    savedPlotId
            );

            webTestClient.post()
                    .uri("/api/crop")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(CropResponseDto.class)
                    .value(response -> {
                        assertThat(response.id()).isNotNull();
                        assertThat(response.name()).isEqualTo("Soja 2026");
                        assertThat(response.plotId()).isEqualTo(savedPlotId);
                    });
        }

        @Test
        @DisplayName("Should return 400 when crops overlap in the same plot")
        void shouldFailWhenOverlapping() {
            Crop obstacle = new Crop();
            obstacle.setName("Safra Ocupada");
            obstacle.setProduct("MILHO");
            obstacle.setStartDate(LocalDate.of(2026, 1, 1));
            obstacle.setEndDate(LocalDate.of(2026, 5, 1));
            obstacle.setPlot(plotRepository.getReferenceById(savedPlotId));
            cropRepository.save(obstacle);

            CropRequestDto conflictRequest = new CropRequestDto(
                    "Safra Intrusiva", "SOJA",
                    LocalDate.of(2026, 4, 15),
                    LocalDate.of(2026, 8, 1),
                    savedPlotId
            );

            webTestClient.post()
                    .uri("/api/crop")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(conflictRequest)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").isEqualTo("O talhão já possui uma safra programada para este período.");
        }
    }
}