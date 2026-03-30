package com.agrobasis.core_service.crop;

import com.agrobasis.core_service.plot.Plot;
import com.agrobasis.core_service.plot.PlotNotFoundException;
import com.agrobasis.core_service.plot.PlotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CropService {

    private final CropRepository cropRepository;
    private final PlotRepository plotRepository;

    @Transactional
    public CropResponseDto createCrop(CropRequestDto request){
        Plot plot = plotRepository.findById(request.plotId()).orElseThrow(() ->
                new PlotNotFoundException("O talhão não foi encontrado"));

        boolean isBusy = cropRepository.existsOverlappingCrop
                (request.plotId(),request.startDate(),request.endDate());

        if (isBusy){
            throw new InvalidCropPeriodException("O talhão já possui uma safra programada para este período.");
        }

        if (request.startDate().isAfter(request.endDate())){
            throw new InvalidCropPeriodException("A data de início da safra está posterior á data do fim");
        }

        Crop crop = new Crop();
        crop.setName(request.name());
        crop.setProduct(request.product());
        crop.setStartDate(request.startDate());
        crop.setEndDate(request.endDate());
        crop.setPlot(plot);

        cropRepository.save(crop);

        return new CropResponseDto(
            crop.getName(),
            crop.getProduct(),
            crop.getStartDate(),
            crop.getEndDate(),
            crop.getId(),
            crop.getPlot().getId()
        );
    }
}
