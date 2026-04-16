package com.agrobasis.core_service.pricing.domain;

import com.agrobasis.core_service.pricing.application.model.PricingInput;
import com.agrobasis.core_service.pricing.application.model.PricingResult;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PricingCalculator {

    private static final int PRICE_SCALE = 2;
    private static final RoundingMode PRICE_ROUNDING_MODE = RoundingMode.HALF_UP;

    public PricingResult calculate(PricingInput input) {
        BigDecimal convertedPrice = scale(input.marketPrice().multiply(input.exchangeRate()));
        BigDecimal adjustedPrice = scale(convertedPrice.subtract(input.costPerTon()));
        BigDecimal netPrice = scale(adjustedPrice.subtract(input.freightPerTon()));
        BigDecimal commercialPrice = scale(netPrice.subtract(input.adjustmentPerTon()));

        return new PricingResult(
                convertedPrice,
                adjustedPrice,
                netPrice,
                commercialPrice
        );
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(PRICE_SCALE, PRICE_ROUNDING_MODE);
    }
}
