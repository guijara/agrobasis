package com.agrobasis.core_service.plot;

import com.agrobasis.core_service.farm.Farm;
import com.agrobasis.core_service.farm.FarmNotFoundException;
import com.agrobasis.core_service.farm.FarmRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    @DisplayName("Should create plot successfully when farm exists")
    void shouldCreatePlotSuccessfully() {
        // Arrange
        UUID farmId = UUID.randomUUID();
        PlotRequestDto request = new PlotRequestDto("Talhão 1", 50.5, farmId);

        Farm mockFarm = new Farm();
        mockFarm.setId(farmId);
        mockFarm.setName("Fazenda Santa Cruz");

        when(farmRepository.findById(farmId)).thenReturn(Optional.of(mockFarm));

        Plot savedPlot = new Plot();
        savedPlot.setId(UUID.randomUUID());
        savedPlot.setName(request.name());
        savedPlot.setHectareArea(request.hectareArea());
        savedPlot.setFarm(mockFarm);

        when(plotRepository.save(any(Plot.class))).thenReturn(savedPlot);

        // Act
        PlotResponseDto result = plotService.createPlot(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Talhão 1");
        assertThat(result.hectareArea()).isEqualTo(50.5);
        assertThat(result.farmId()).isEqualTo(farmId);

        verify(farmRepository, times(1)).findById(farmId);
        verify(plotRepository, times(1)).save(any(Plot.class));
    }
}