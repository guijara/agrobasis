package com.agrobasis.core_service.config;

import com.agrobasis.core_service.organization.OrganizationAlreadyExistsException;
import com.agrobasis.core_service.organization.OrganizationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                null // Sem detalhes granulares para este erro
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(OrganizationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrganizationNotFound(OrganizationNotFoundException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        // CORRIGIDO: O status do retorno agora é NOT_FOUND
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // Trata falhas de validação de DTO (Bad Request 400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {

        // Extrai os erros de todos os campos que falharam na validação
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

        // Em um sistema real, é imperativo logar a 'ex' aqui (ex: log.error("Erro interno", ex))

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ocorreu um erro interno inesperado no servidor.",
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
