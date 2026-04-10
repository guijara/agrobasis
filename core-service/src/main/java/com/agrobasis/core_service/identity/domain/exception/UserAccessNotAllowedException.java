package com.agrobasis.core_service.identity.domain.exception;

public class UserAccessNotAllowedException extends RuntimeException {
    public UserAccessNotAllowedException(String message) {
        super(message);
    }
}
