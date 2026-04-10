package com.agrobasis.core_service.identity.domain.exception;

public class MembershipRequestAlreadyExistsException extends RuntimeException {
    public MembershipRequestAlreadyExistsException(String message) {
        super(message);
    }
}
