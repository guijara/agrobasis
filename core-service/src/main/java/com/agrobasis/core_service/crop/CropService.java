package com.agrobasis.core_service.crop;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CropService {

    private final CropRepository cropRepository;
}
