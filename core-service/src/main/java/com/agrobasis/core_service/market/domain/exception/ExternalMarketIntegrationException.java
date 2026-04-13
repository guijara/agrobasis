package com.agrobasis.core_service.market.domain.exception;

public class ExternalMarketIntegrationException extends RuntimeException {
    public ExternalMarketIntegrationException(String message) {
        super(message);
    }

    public ExternalMarketIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
