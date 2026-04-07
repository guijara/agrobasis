package com.agrobasis.core_service.pricing.domain.exception;

public class ExchangeRateUnavailableException extends RuntimeException {
    public ExchangeRateUnavailableException(String message) {
        super(message);
    }
}
