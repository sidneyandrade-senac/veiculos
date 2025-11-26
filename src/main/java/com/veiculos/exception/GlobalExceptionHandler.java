package com.veiculos.exception;

import com.veiculos.exception.custom.BusinessRuleException;
import com.veiculos.exception.custom.ResourceAlreadyExistsException;
import com.veiculos.exception.custom.ResourceNotFoundException;
import com.veiculos.exception.custom.ValidationException;
import com.veiculos.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Manipulador global de exceções da API.
 * 
 * Captura todas as exceções lançadas pelos controllers e as transforma
 * em respostas padronizadas usando ApiResponse.
 * 
 * Cada handler retorna ResponseEntity<ApiResponse<Void>> com:
 * - Código HTTP apropriado
 * - Código de erro único (ErrorCode)
 * - Mensagem descritiva
 * - Path da requisição que gerou o erro
 * - Lista de erros detalhados (quando aplicável)
 * 
 * @author Sistema de Veículos
 * @version 2.0
 * @since 26/11/2025
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Constrói resposta de erro padronizada usando ApiResponse.
     * 
     * @param status código HTTP
     * @param errorCode código único do erro
     * @param message mensagem descritiva
     * @param request requisição HTTP
     * @param errors lista de erros detalhados (opcional)
     * @return ResponseEntity com ApiResponse de erro
     */
    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(
            HttpStatus status,
            ErrorCode errorCode,
            String message,
            HttpServletRequest request,
            List<ApiResponse.ErrorDetail> errors) {
        
        // Adiciona o código de erro como primeiro item da lista se não houver erros
        List<ApiResponse.ErrorDetail> errorList = errors;
        if (errorList == null) {
            errorList = new ArrayList<>();
        }
        
        // Sempre inclui o código de erro principal
        if (errorList.isEmpty()) {
            errorList.add(ApiResponse.ErrorDetail.of(errorCode.getCode(), message));
        }
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .status(status.value())
                .message(message)
                .timestamp(Instant.now().toString())
                .path(request.getRequestURI())
                .errors(errorList)
                .build();
        
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Sobrecarga sem lista de erros.
     */
    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(
            HttpStatus status,
            ErrorCode errorCode,
            String message,
            HttpServletRequest request) {
        
        return buildErrorResponse(status, errorCode, message, request, null);
    }

    // ========== CUSTOM EXCEPTIONS HANDLERS ==========

    /**
     * Handler para ResourceNotFoundException.
     * Retorna HTTP 404 quando recurso não é encontrado.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        
        log.debug("Recurso não encontrado: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND,
                ex.getMessage(),
                request
        );
    }

    /**
     * Handler para ResourceAlreadyExistsException.
     * Retorna HTTP 409 quando há tentativa de criar recurso duplicado.
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceAlreadyExists(
            ResourceAlreadyExistsException ex,
            HttpServletRequest request) {
        
        log.warn("Tentativa de criar recurso duplicado: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ErrorCode.RESOURCE_ALREADY_EXISTS,
                ex.getMessage(),
                request
        );
    }

    /**
     * Handler para ValidationException.
     * Retorna HTTP 400 com lista de erros de validação.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {
        
        log.debug("Erro de validação: {} erros encontrados - Path: {}", 
                ex.getErrorCount(), request.getRequestURI());
        
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR,
                ex.getMessage(),
                request,
                ex.getErrors()
        );
    }

    /**
     * Handler para BusinessRuleException.
     * Retorna HTTP 422 quando há violação de regra de negócio.
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRule(
            BusinessRuleException ex,
            HttpServletRequest request) {
        
        log.warn("Violação de regra de negócio: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        
        return buildErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ErrorCode.BUSINESS_RULE_VIOLATION,
                ex.getMessage(),
                request
        );
    }

    // ========== SPRING FRAMEWORK EXCEPTIONS ==========

    /**
     * Handler para MethodArgumentNotValidException.
     * Captura erros de Bean Validation (@Valid).
     * Retorna HTTP 400 com lista de campos inválidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        log.debug("Validação de argumentos falhou: {} erros - Path: {}", 
                ex.getBindingResult().getErrorCount(), request.getRequestURI());
        
        List<ApiResponse.ErrorDetail> errors = new ArrayList<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.add(ApiResponse.ErrorDetail.fieldError(
                    "VALIDATION_ERROR",
                    error.getField(),
                    error.getDefaultMessage(),
                    error.getRejectedValue()
            ));
        });
        
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR,
                "Dados de entrada inválidos",
                request,
                errors
        );
    }

    /**
     * Handler para DataIntegrityViolationException.
     * Captura violações de constraint do banco de dados.
     * Retorna HTTP 409.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {
        
        log.error("Violação de integridade de dados - Path: {} - Cause: {}", 
                request.getRequestURI(), ex.getMostSpecificCause().getMessage());
        
        String message = "Violação de integridade de dados";
        
        // Tentar identificar o tipo de violação
        String causeMessage = ex.getMostSpecificCause().getMessage().toLowerCase();
        if (causeMessage.contains("unique") || causeMessage.contains("duplicate")) {
            message = "Registro duplicado. Este recurso já existe no sistema.";
        } else if (causeMessage.contains("foreign key") || causeMessage.contains("constraint")) {
            message = "Não é possível realizar esta operação. Existem registros dependentes.";
        }
        
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ErrorCode.DATA_INTEGRITY_VIOLATION,
                message,
                request
        );
    }

    /**
     * Handler para PropertyReferenceException.
     * Captura tentativa de ordenar/filtrar por propriedade inexistente.
     * Retorna HTTP 400.
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ApiResponse<Void>> handlePropertyReference(
            PropertyReferenceException ex,
            HttpServletRequest request) {
        
        log.debug("Propriedade de ordenação inválida: {} - Path: {}", 
                ex.getPropertyName(), request.getRequestURI());
        
        String message = String.format(
                "Propriedade '%s' não existe para ordenação/filtro",
                ex.getPropertyName()
        );
        
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_PROPERTY,
                message,
                request
        );
    }

    /**
     * Handler para IllegalArgumentException.
     * Captura argumentos inválidos não tratados por outros handlers.
     * Retorna HTTP 400.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        
        log.debug("Argumento ilegal: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_ARGUMENT,
                ex.getMessage(),
                request
        );
    }

    // ========== FALLBACK HANDLER ==========

    /**
     * Handler genérico para exceções não tratadas especificamente.
     * Retorna HTTP 500.
     * 
     * IMPORTANTE: Este handler deve ser o último da hierarquia,
     * pois captura todas as exceções não tratadas pelos handlers anteriores.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Erro inesperado não tratado - Path: {} - Exception: {}", 
                request.getRequestURI(), ex.getClass().getSimpleName(), ex);
        
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor. Por favor, contate o suporte.",
                request
        );
    }
}
