package com.agrobasis.core_service.farm.application;

import com.agrobasis.core_service.farm.domain.Farm;
import com.agrobasis.core_service.farm.domain.FarmNotFoundException;
import com.agrobasis.core_service.farm.infrastructure.FarmRepository;
import com.agrobasis.core_service.farm.api.dto.PlotCreateRequest;
import com.agrobasis.core_service.farm.api.dto.PlotResponse;
import com.agrobasis.core_service.farm.api.dto.PlotUpdateRequest;
import com.agrobasis.core_service.farm.domain.Plot;
import com.agrobasis.core_service.farm.domain.PlotNotFoundException;
import com.agrobasis.core_service.farm.infrastructure.PlotRepository;
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

    public PlotResponse createPlot(PlotCreateRequest plotCreateRequest){

        Farm farm = farmRepository.findById(plotCreateRequest.farmId())
                .orElseThrow(() -> new FarmNotFoundException("Fazenda não encontrada"));

        Plot newPlot = new Plot();
        newPlot.setName(plotCreateRequest.name());
        newPlot.setHectareArea(plotCreateRequest.hectareArea());
        newPlot.setFarm(farm);

        plotRepository.save(newPlot);

        return new PlotResponse(
                newPlot.getId(),
                newPlot.getName(),
                newPlot.getHectareArea(),
                newPlot.getFarm().getId()
        );
    }

    public PlotResponse getPlotById(UUID id){
        Plot plot = plotRepository.findById(id).orElseThrow(
                () -> new PlotNotFoundException("Talhão não encontrado"));

        return new PlotResponse(
                plot.getId(),
                plot.getName(),
                plot.getHectareArea(),
                plot.getFarm().getId()

        );
    }

    public Page<PlotResponse> getAllPlotsByOrganization(UUID farmId, Pageable pageable){
        Page<Plot> plots = plotRepository.findAllByFarm_Id(farmId,pageable);

        return plots.map(plot -> new PlotResponse(
                plot.getId(),
                plot.getName(),
                plot.getHectareArea(),
                plot.getFarm().getId()
        ));
    }

    @Transactional
    public PlotResponse updatePlot(UUID id, PlotUpdateRequest plotUpdateRequest){
        Plot plot = plotRepository.findById(id).orElseThrow(
                () -> new PlotNotFoundException("O talhão não existe."));

        plot.setName(plotUpdateRequest.name());
        plot.setHectareArea(plotUpdateRequest.hectareArea());
        plotRepository.save(plot);

        return new PlotResponse(
            plot.getId(),
            plot.getName(),
            plot.getHectareArea(),
            plot.getFarm().getId()
        );
    }
}
