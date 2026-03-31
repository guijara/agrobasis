package com.agrobasis.core_service.crop;

import com.agrobasis.core_service.plot.Plot;
import com.agrobasis.core_service.plot.PlotNotFoundException;
import com.agrobasis.core_service.plot.PlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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

    @Transactional(readOnly = true)
    public CropResponseDto getCropById(UUID id){
        Crop response = cropRepository.findById(id).orElseThrow
                (() -> new CropNotFoundException("A safra não foi encontrada."));

        return new CropResponseDto(
                response.getName(),
                response.getProduct(),
                response.getStartDate(),
                response.getEndDate(),
                response.getId(),
                response.getPlot().getId()
        );
    }

    @Transactional(readOnly = true)
    public Page<CropResponseDto> ListCropByPlot(UUID plotId, Pageable pageable){
        Page<Crop> response = cropRepository.findAllByPlot_Id(plotId,pageable);
        return response.map(crop -> new CropResponseDto(
                crop.getName(),
                crop.getProduct(),
                crop.getStartDate(),
                crop.getEndDate(),
                crop.getId(),
                crop.getPlot().getId()
        ));
    }
}
