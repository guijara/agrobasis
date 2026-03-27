package com.agrobasis.core_service.plot;

import com.agrobasis.core_service.farm.Farm;
import com.agrobasis.core_service.farm.FarmNotFoundException;
import com.agrobasis.core_service.farm.FarmRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlotService {

    private final PlotRepository plotRepository;
    private final FarmRepository farmRepository;

    public PlotResponseDto createPlot(PlotCreateRequestDto plotCreateRequestDto){

        Farm farm = farmRepository.findById(plotCreateRequestDto.farmId())
                .orElseThrow(() -> new FarmNotFoundException("Fazenda não encontrada"));

        Plot newPlot = new Plot();
        newPlot.setName(plotCreateRequestDto.name());
        newPlot.setHectareArea(plotCreateRequestDto.hectareArea());
        newPlot.setFarm(farm);

        plotRepository.save(newPlot);

        return new PlotResponseDto(
                newPlot.getId(),
                newPlot.getName(),
                newPlot.getHectareArea(),
                newPlot.getFarm().getId()
        );
    }

    public PlotResponseDto getPlotById(UUID id){
        Plot plot = plotRepository.findById(id).orElseThrow(
                () -> new PlotNotFoundException("Talhão não encontrado"));

        return new PlotResponseDto(
                plot.getId(),
                plot.getName(),
                plot.getHectareArea(),
                plot.getFarm().getId()

        );
    }

    public Page<PlotResponseDto> getAllPlotsByOrganization(UUID farmId,Pageable pageable){
        Page<Plot> plots = plotRepository.findAllByFarm_Id(farmId,pageable);

        return plots.map(plot -> new PlotResponseDto(
                plot.getId(),
                plot.getName(),
                plot.getHectareArea(),
                plot.getFarm().getId()
        ));
    }

    @Transactional
    public PlotResponseDto updatePlot(UUID id, PlotUpdateRequestDto plotUpdateRequestDto){
        Plot plot = plotRepository.findById(id).orElseThrow(
                () -> new PlotNotFoundException("O talhão não existe."));

        plot.setName(plotUpdateRequestDto.name());
        plot.setHectareArea(plotUpdateRequestDto.hectareArea());
        plotRepository.save(plot);

        return new PlotResponseDto(
            plot.getId(),
            plot.getName(),
            plot.getHectareArea(),
            plot.getFarm().getId()
        );
    }
}
