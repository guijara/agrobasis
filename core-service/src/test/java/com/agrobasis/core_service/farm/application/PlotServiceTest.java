package com.agrobasis.core_service.farm.application;

import com.agrobasis.core_service.farm.domain.Farm;
import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.farm.domain.exception.FarmNotFoundException;
import com.agrobasis.core_service.farm.api.dto.PlotCreateRequest;
import com.agrobasis.core_service.farm.api.dto.PlotResponse;
import com.agrobasis.core_service.farm.api.dto.PlotUpdateRequest;
import com.agrobasis.core_service.farm.domain.Plot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.agrobasis.core_service.farm.infrastructure.FarmRepository;
import com.agrobasis.core_service.farm.infrastructure.PlotRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlotServiceTest {

    @Mock
    private PlotRepository plotRepository;

    @Mock
    private FarmRepository farmRepository;

    @InjectMocks
    private PlotService plotService;

    @Nested
    @DisplayName("createPlot()")
    class CreatePlotTests {

        @Test
        @DisplayName("Should create plot successfully")
        void shouldCreatePlotSuccessfully() {
            UUID farmId = UUID.randomUUID();
            PlotCreateRequest request = new PlotCreateRequest("Talhão", 50.0, Commodity.SOYBEAN, farmId);

            Farm mockFarm = new Farm();
            mockFarm.setId(farmId);

            when(farmRepository.findById(farmId)).thenReturn(Optional.of(mockFarm));
            when(plotRepository.save(any(Plot.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PlotResponse result = plotService.createPlot(request);

            assertThat(result.name()).isEqualTo("Talhão");
            assertThat(result.commodity()).isEqualTo(Commodity.SOYBEAN);
            assertThat(result.farmId()).isEqualTo(farmId);
            verify(plotRepository).save(any(Plot.class));
        }

        @Test
        @DisplayName("Should throw exception when farm not found")
        void shouldThrowExceptionWhenFarmNotFound() {
            UUID farmId = UUID.randomUUID();
            PlotCreateRequest request = new PlotCreateRequest("T1", 10.0, Commodity.CORN, farmId);

            when(farmRepository.findById(farmId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> plotService.createPlot(request))
                    .isInstanceOf(FarmNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getPlotById()")
    class GetPlotByIdTests {

        @Test
        @DisplayName("Should return plot when ID exists")
        void shouldReturnPlotWhenIdExists() {
            UUID id = UUID.randomUUID();
            UUID farmId = UUID.randomUUID();
            Plot plot = new Plot();
            plot.setId(id);
            plot.setName("Talhão");
            plot.setHectareArea(42.5);
            plot.setCommodity(Commodity.SOYBEAN);
            Farm farm = new Farm();
            farm.setId(farmId);
            plot.setFarm(farm);

            when(plotRepository.findById(id)).thenReturn(Optional.of(plot));

            PlotResponse result = plotService.getPlotById(id);

            assertThat(result.id()).isEqualTo(id);
            assertThat(result.name()).isEqualTo("Talhão");
            assertThat(result.hectareArea()).isEqualTo(42.5);
            assertThat(result.commodity()).isEqualTo(Commodity.SOYBEAN);
            assertThat(result.farmId()).isEqualTo(farmId);
        }
    }

    @Nested
    @DisplayName("getAllPlotsByOrganization()")
    class ListPlotsTests {

        @Test
        @DisplayName("Should return paginated plots by farm ID")
        void shouldReturnPaginatedPlots() {
            UUID farmId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);
            Plot plot = new Plot();
            plot.setId(UUID.randomUUID());
            plot.setName("Talhão Norte");
            plot.setHectareArea(80.0);
            plot.setCommodity(Commodity.CORN);
            Farm farm = new Farm();
            farm.setId(farmId);
            plot.setFarm(farm);

            Page<Plot> page = new PageImpl<>(List.of(plot), pageable, 1);
            when(plotRepository.findAllByFarm_Id(eq(farmId), any(Pageable.class))).thenReturn(page);

            Page<PlotResponse> result = plotService.getAllPlotsByOrganization(farmId, pageable);

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().getFirst().commodity()).isEqualTo(Commodity.CORN);
            assertThat(result.getContent().getFirst().farmId()).isEqualTo(farmId);
            verify(plotRepository).findAllByFarm_Id(farmId, pageable);
        }
    }

    @Nested
    @DisplayName("updatePlot()")
    class UpdatePlotTests {

        @Test
        @DisplayName("Should update plot data")
        void shouldUpdatePlotData() {
            UUID id = UUID.randomUUID();
            UUID farmId = UUID.randomUUID();
            Plot existingPlot = new Plot();
            existingPlot.setId(id);
            existingPlot.setName("Nome Antigo");
            existingPlot.setHectareArea(50.0);
            existingPlot.setCommodity(Commodity.SOYBEAN);
            Farm farm = new Farm();
            farm.setId(farmId);
            existingPlot.setFarm(farm);

            PlotUpdateRequest request = new PlotUpdateRequest("Nome Atualizado", 75.0, Commodity.CORN);
            when(plotRepository.findById(id)).thenReturn(Optional.of(existingPlot));

            PlotResponse result = plotService.updatePlot(id, request);

            assertThat(result.name()).isEqualTo("Nome Atualizado");
            assertThat(result.hectareArea()).isEqualTo(75.0);
            assertThat(result.commodity()).isEqualTo(Commodity.CORN);
            assertThat(result.farmId()).isEqualTo(farmId);
            verify(plotRepository).save(existingPlot);
        }
    }
}
