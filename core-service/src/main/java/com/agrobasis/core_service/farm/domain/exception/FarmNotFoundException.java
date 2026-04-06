package com.agrobasis.core_service.farm.domain.exception;

public class FarmNotFoundException extends RuntimeException {
    public FarmNotFoundException(String message) {
        super(message);
    }
}
