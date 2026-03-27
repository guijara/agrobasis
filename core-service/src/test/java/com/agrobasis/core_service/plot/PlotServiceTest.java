package com.agrobasis.core_service.plot;

import com.agrobasis.core_service.farm.Farm;
import com.agrobasis.core_service.farm.FarmNotFoundException;
import com.agrobasis.core_service.farm.FarmRepository;
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
            PlotCreateRequestDto request = new PlotCreateRequestDto("Talhão", 50.0, farmId);

            Farm mockFarm = new Farm();
            mockFarm.setId(farmId);

            when(farmRepository.findById(farmId)).thenReturn(Optional.of(mockFarm));
            when(plotRepository.save(any(Plot.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PlotResponseDto result = plotService.createPlot(request);

            assertThat(result.name()).isEqualTo("Talhão");
            assertThat(result.farmId()).isEqualTo(farmId);
            verify(plotRepository).save(any(Plot.class));
        }

        @Test
        @DisplayName("Should throw exception when farm not found")
        void shouldThrowExceptionWhenFarmNotFound() {
            UUID farmId = UUID.randomUUID();
            PlotCreateRequestDto request = new PlotCreateRequestDto("T1", 10.0, farmId);

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
            Plot plot = new Plot();
            plot.setId(id);
            plot.setName("Talhão");
            plot.setFarm(new Farm());

            when(plotRepository.findById(id)).thenReturn(Optional.of(plot));

            PlotResponseDto result = plotService.getPlotById(id);

            assertThat(result.id()).isEqualTo(id);
            assertThat(result.name()).isEqualTo("Talhão");
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
            plot.setFarm(new Farm());

            Page<Plot> page = new PageImpl<>(List.of(plot), pageable, 1);
            when(plotRepository.findAllByFarm_Id(eq(farmId), any(Pageable.class))).thenReturn(page);

            Page<PlotResponseDto> result = plotService.getAllPlotsByOrganization(farmId, pageable);

            assertThat(result.getTotalElements()).isEqualTo(1);
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
            Plot existingPlot = new Plot();
            existingPlot.setId(id);
            existingPlot.setFarm(new Farm());

            PlotUpdateRequestDto request = new PlotUpdateRequestDto("Nome Atualizado", 75.0);
            when(plotRepository.findById(id)).thenReturn(Optional.of(existingPlot));

            PlotResponseDto result = plotService.updatePlot(id, request);

            assertThat(result.name()).isEqualTo("Nome Atualizado");
            assertThat(result.hectareArea()).isEqualTo(75.0);
            verify(plotRepository).save(existingPlot);
        }
    }
}