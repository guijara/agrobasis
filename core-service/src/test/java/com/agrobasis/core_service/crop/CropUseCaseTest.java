package com.agrobasis.core_service.crop;

import com.agrobasis.core_service.config.ErrorResponse;
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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CropUseCaseTest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

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
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        cropRepository.deleteAll();
        plotRepository.deleteAll();
        farmRepository.deleteAll();
        organizationRepository.deleteAll();

        Organization organization = new Organization();
        organization.setName("AgroTech");
        organization.setCnpj("123");
        organization.setLocation("Cuiabá");
        organizationRepository.save(organization);

        Farm farm = new Farm();
        farm.setName("Fazenda Modelo");
        farm.setLocation("MT-251");
        farm.setHectareArea(1000.0);
        farm.setOrganization(organization);
        farm = farmRepository.save(farm);

        Plot plot = new Plot();
        plot.setName("Talhão 01");
        plot.setHectareArea(50.0);
        plot.setFarm(farm);
        plot = plotRepository.save(plot);

        this.savedPlotId = plot.getId();
    }

    @Nested
    @DisplayName("Cenários de POST")
    class PostScenarios {

        @Test
        @DisplayName("Deve criar safra com sucesso")
        void shouldCreateCrop() {
            CropRequestDto request = new CropRequestDto(
                    "Soja 2026", "SOJA",
                    LocalDate.of(2026, 10, 1),
                    LocalDate.of(2027, 2, 1),
                    savedPlotId
            );

            var response = restClient.post()
                    .uri("/api/crop")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toEntity(CropResponseDto.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody().name()).isEqualTo("Soja 2026");
            assertThat(response.getBody().id()).isNotNull();
        }

        @Test
        @DisplayName("Deve barrar sobreposição de datas")
        void shouldFailWhenOverlapping() {
            Crop obstacle = new Crop();
            obstacle.setName("Safra Existente");
            obstacle.setProduct("MILHO");
            obstacle.setStartDate(LocalDate.of(2026, 1, 1));
            obstacle.setEndDate(LocalDate.of(2026, 5, 1));
            obstacle.setPlot(plotRepository.getReferenceById(savedPlotId));
            cropRepository.save(obstacle);

            CropRequestDto conflictRequest = new CropRequestDto(
                    "Safra Conflito", "SOJA",
                    LocalDate.of(2026, 4, 15),
                    LocalDate.of(2026, 8, 1),
                    savedPlotId
            );

            var response = restClient.post()
                    .uri("/api/crop")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(conflictRequest)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), (req, res) -> {
                    })
                    .toEntity(ErrorResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().message()).isEqualTo("O talhão já possui uma safra programada para este período.");
        }
    }

    @Nested
    @DisplayName("Cenários de GET")
    class GetScenarios {

        @Test
        @DisplayName("Deve listar safras de um talhão com paginação")
        void shouldListCropsByPlot() {
            // Arrange
            createTestCrop("Safra A", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 5, 1));
            createTestCrop("Safra B", LocalDate.of(2026, 6, 1), LocalDate.of(2026, 11, 1));

            // Act
            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/crop")
                            .queryParam("plotId", savedPlotId)
                            .build())
                    .retrieve()
                    .toEntity(String.class);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).contains("Safra A");
            assertThat(response.getBody()).contains("Safra B");
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar safra inexistente")
        void shouldReturn404WhenNotFound() {
            var response = restClient.get()
                    .uri("/api/crop/{id}", UUID.randomUUID())
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
                    .toEntity(ErrorResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    private void createTestCrop(String name, LocalDate start, LocalDate end) {
        Crop crop = new Crop();
        crop.setName(name);
        crop.setProduct("SOJA");
        crop.setStartDate(start);
        crop.setEndDate(end);
        crop.setPlot(plotRepository.getReferenceById(savedPlotId));
        cropRepository.save(crop);
    }
}