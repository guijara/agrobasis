package com.agrobasis.core_service.shared.api.error;

import com.agrobasis.core_service.cost.domain.exception.CostProfileAlreadyExistsException;
import com.agrobasis.core_service.cost.domain.exception.CostProfileNotFoundException;
import com.agrobasis.core_service.farm.domain.exception.FarmNotFoundException;
import com.agrobasis.core_service.farm.domain.exception.PlotNotFoundException;
import com.agrobasis.core_service.identity.domain.exception.InvalidCredentialsException;
import com.agrobasis.core_service.identity.domain.exception.MembershipRequestAlreadyExistsException;
import com.agrobasis.core_service.identity.domain.exception.MembershipRequestNotFoundException;
import com.agrobasis.core_service.identity.domain.exception.UnauthorizedOrganizationApprovalException;
import com.agrobasis.core_service.identity.domain.exception.UserAccessNotAllowedException;
import com.agrobasis.core_service.identity.domain.exception.UserEmailAlreadyExistsException;
import com.agrobasis.core_service.identity.domain.exception.UserNotFoundException;
import com.agrobasis.core_service.market.domain.exception.ExchangeRateNotFoundException;
import com.agrobasis.core_service.market.domain.exception.MarketQuoteNotFoundException;
import com.agrobasis.core_service.organization.domain.exception.OrganizationAlreadyExistsException;
import com.agrobasis.core_service.organization.domain.exception.OrganizationNotFoundException;
import com.agrobasis.core_service.pricing.domain.exception.CostProfileUnavailableException;
import com.agrobasis.core_service.pricing.domain.exception.ExchangeRateUnavailableException;
import com.agrobasis.core_service.pricing.domain.exception.MarketQuoteUnavailableException;
import com.agrobasis.core_service.pricing.domain.exception.UnsupportedPricingContextException;
import com.agrobasis.core_service.shared.domain.exception.TenantAccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrganizationAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleOrganizationAlreadyExists(OrganizationAlreadyExistsException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(OrganizationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrganizationNotFound(OrganizationNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(FarmNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFarmNotFound(FarmNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(PlotNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlotNotFound(PlotNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(CostProfileAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCostProfileAlreadyExistsException(CostProfileAlreadyExistsException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(CostProfileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCostProfileNotFoundException(CostProfileNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(UserEmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserEmailAlreadyExistsException(UserEmailAlreadyExistsException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(UserAccessNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleUserAccessNotAllowedException(UserAccessNotAllowedException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(MembershipRequestAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleMembershipRequestAlreadyExistsException(MembershipRequestAlreadyExistsException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(MembershipRequestNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMembershipRequestNotFoundException(MembershipRequestNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedOrganizationApprovalException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedOrganizationApprovalException(UnauthorizedOrganizationApprovalException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(TenantAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleTenantAccessDeniedException(TenantAccessDeniedException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(MarketQuoteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMarketQuoteNotFoundException(MarketQuoteNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(ExchangeRateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExchangeRateNotFoundException(ExchangeRateNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MarketQuoteUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleMarketQuoteUnavailableException(MarketQuoteUnavailableException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(ExchangeRateUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleExchangeRateUnavailableException(ExchangeRateUnavailableException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(CostProfileUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleCostProfileUnavailableException(CostProfileUnavailableException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(UnsupportedPricingContextException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedPricingContextException(UnsupportedPricingContextException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorResponse.FieldErrorDetail> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldErrorDetail(error.getField(), error.getDefaultMessage()))
                .toList();

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "A requisição contém parâmetros inválidos.",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno inesperado no servidor.", request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(status).body(errorResponse);
    }
}
