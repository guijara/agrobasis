package com.agrobasis.core_service.pricing.domain;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.Unit;
import com.agrobasis.core_service.pricing.application.model.PricingInput;
import com.agrobasis.core_service.pricing.application.model.PricingResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PricingCalculatorTest {

    private final PricingCalculator pricingCalculator = new PricingCalculator();

    @Test
    @DisplayName("Should calculate pure pricing result")
    void shouldCalculatePurePricingResult() {
        PricingInput input = new PricingInput(
                Commodity.SOYBEAN,
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                new BigDecimal("132.45"),
                Currency.USD,
                Unit.TON,
                new BigDecimal("5.421300"),
                new BigDecimal("45.00"),
                new BigDecimal("20.00"),
                new BigDecimal("10.00")
        );

        PricingResult result = pricingCalculator.calculate(input);

        assertThat(result.convertedPrice()).isEqualByComparingTo("718.05");
        assertThat(result.adjustedPrice()).isEqualByComparingTo("673.05");
        assertThat(result.netPrice()).isEqualByComparingTo("653.05");
        assertThat(result.commercialPrice()).isEqualByComparingTo("643.05");
    }
}
