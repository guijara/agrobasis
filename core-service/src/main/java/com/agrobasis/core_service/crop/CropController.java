package com.agrobasis.core_service.crop;

import com.agrobasis.core_service.config.ApiStandardErrors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crop")
@RequiredArgsConstructor
@ApiStandardErrors
public class CropController {

    private final CropService cropService;
}
