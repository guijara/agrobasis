package com.agrobasis.core_service.pricing.application;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.pricing.api.dto.CurrentPricingAnalysisResponse;
import com.agrobasis.core_service.pricing.api.dto.CurrentPricingResponse;
import com.agrobasis.core_service.pricing.api.dto.PricingCompositionResponse;
import com.agrobasis.core_service.pricing.api.dto.PricingImpactSummaryResponse;
import com.agrobasis.core_service.pricing.api.dto.PricingIndicatorResponse;
import com.agrobasis.core_service.pricing.domain.exception.PricingAnalysisUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PricingAnalysisService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final String PERCENT = "PERCENT";

    private final PricingService pricingService;

    public CurrentPricingAnalysisResponse analyzeCurrentPricing(UUID organizationId, UUID farmId, Commodity commodity) {
        CurrentPricingResponse pricing = pricingService.calculateCurrentPrice(organizationId, farmId, commodity);

        BigDecimal convertedPrice = pricing.convertedPrice();
        BigDecimal costPerTon = pricing.costPerTon();
        BigDecimal freightPerTon = pricing.freightPerTon();
        BigDecimal adjustmentPerTon = pricing.adjustmentPerTon();
        BigDecimal commercialPrice = pricing.commercialPrice();
        BigDecimal totalReduction = costPerTon.add(freightPerTon).add(adjustmentPerTon);

        PricingCompositionResponse composition = new PricingCompositionResponse(
                pricing.marketQuote().price(),
                pricing.exchangeRate().rate(),
                convertedPrice,
                costPerTon,
                freightPerTon,
                adjustmentPerTon,
                commercialPrice
        );

        PricingImpactSummaryResponse impactSummary = new PricingImpactSummaryResponse(
                totalReduction,
                convertedPrice.subtract(commercialPrice),
                costPerTon,
                freightPerTon,
                adjustmentPerTon
        );

        List<PricingIndicatorResponse> indicators = List.of(
                indicator("cost_share_of_converted_price", calculatePercentage(costPerTon, convertedPrice)),
                indicator("freight_share_of_converted_price", calculatePercentage(freightPerTon, convertedPrice)),
                indicator("commercial_adjustment_share_of_converted_price", calculatePercentage(adjustmentPerTon, convertedPrice)),
                indicator("commercial_price_retention", calculatePercentage(commercialPrice, convertedPrice)),
                indicator("total_reduction_share_of_converted_price", calculatePercentage(totalReduction, convertedPrice))
        );

        return new CurrentPricingAnalysisResponse(
                pricing.commodity(),
                pricing.farmId(),
                convertedPrice,
                pricing.adjustedPrice(),
                pricing.netPrice(),
                commercialPrice,
                composition,
                impactSummary,
                indicators,
                pricing.calculatedAt()
        );
    }

    private PricingIndicatorResponse indicator(String name, BigDecimal value) {
        return new PricingIndicatorResponse(name, value, PERCENT);
    }

    private BigDecimal calculatePercentage(BigDecimal part, BigDecimal base) {
        if (base.compareTo(BigDecimal.ZERO) == 0) {
            throw new PricingAnalysisUnavailableException("Não é possível calcular indicadores analíticos com preço convertido igual a zero.");
        }
        return part
                .multiply(ONE_HUNDRED)
                .divide(base, 2, RoundingMode.HALF_UP);
    }
}
