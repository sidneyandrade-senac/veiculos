package com.veiculos.exception;

import com.veiculos.exception.custom.BusinessRuleException;
import com.veiculos.exception.custom.ResourceAlreadyExistsException;
import com.veiculos.exception.custom.ResourceNotFoundException;
import com.veiculos.exception.custom.ValidationException;
import com.veiculos.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testes para GlobalExceptionHandler.
 * 
 * Valida todos os handlers de exceï¿½ï¿½o e a transformaï¿½ï¿½o correta
 * em respostas ApiResponse padronizadas.
 * 
 * @author Sistema de Veï¿½culos
 * @version 1.0
 * @since 26/11/2025
 */
@DisplayName("GlobalExceptionHandler - Testes de Manipulaï¿½ï¿½o de Exceï¿½ï¿½es")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Nested
    @DisplayName("Testes para Exceï¿½ï¿½es Customizadas")
    class CustomExceptionsTests {

        @Test
        @DisplayName("Deve tratar ResourceNotFoundException com HTTP 404")
        void deveRetornar404QuandoRecursoNaoEncontrado() {
            // Arrange
            ResourceNotFoundException exception = new ResourceNotFoundException("Fabricante", 1L);

            // Act
            ResponseEntity<ApiResponse<Void>> response = handler.handleResourceNotFound(exception, request);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            
            ApiResponse<Void> body = response.getBody();
            assertNotNull(body);
            assertFalse(body.isSuccess());
            assertEquals(404, body.getStatus());
            assertTrue(body.getMessage().contains("Fabricante"));
            assertNotNull(body.getErrors());
            assertFalse(body.getErrors().isEmpty());
            assertEquals("RESOURCE_NOT_FOUND", body.getErrors().get(0).getCode());
        }

        @Test
        @DisplayName("Deve tratar ResourceAlreadyExistsException com HTTP 409")
        void deveRetornar409QuandoRecursoJaExiste() {
            // Arrange
            ResourceAlreadyExistsException exception = 
                new ResourceAlreadyExistsException("Fabricante", "nome", "Ford");

            // Act
            ResponseEntity<ApiResponse<Void>> response = handler.handleResourceAlreadyExists(exception, request);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            
            ApiResponse<Void> body = response.getBody();
            assertNotNull(body);
            assertFalse(body.isSuccess());
            assertEquals(409, body.getStatus());
            assertNotNull(body.getErrors());
            assertEquals("RESOURCE_ALREADY_EXISTS", body.getErrors().get(0).getCode());
        }

        @Test
        @DisplayName("Deve tratar ValidationException com HTTP 400 e mï¿½ltiplos erros")
        void deveRetornar400QuandoErroValidacao() {
            // Arrange
            ValidationException exception = new ValidationException("Dados invï¿½lidos")
                .addFieldError("placa", "Placa invï¿½lida")
                .addFieldError("ano", "Ano deve ser maior que 1900");

            // Act
            ResponseEntity<ApiResponse<Void>> response = handler.handleValidationException(exception, request);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            
            ApiResponse<Void> body = response.getBody();
            assertNotNull(body);
            assertFalse(body.isSuccess());
            assertEquals(400, body.getStatus());
            assertNotNull(body.getErrors());
            assertTrue(body.getErrors().size() >= 2);
        }

        @Test
        @DisplayName("Deve tratar BusinessRuleException com HTTP 422")
        void deveRetornar422QuandoViolacaoRegraDeNegocio() {
            // Arrange
            BusinessRuleException exception = 
                BusinessRuleException.cannotDeleteWithDependents("Fabricante", "modelos");

            // Act
            ResponseEntity<ApiResponse<Void>> response = handler.handleBusinessRule(exception, request);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            
            ApiResponse<Void> body = response.getBody();
            assertNotNull(body);
            assertFalse(body.isSuccess());
            assertEquals(422, body.getStatus());
            assertNotNull(body.getErrors());
            assertEquals("BUSINESS_RULE_VIOLATION", body.getErrors().get(0).getCode());
        }
    }

    @Nested
    @DisplayName("Testes para Exceï¿½ï¿½es do Spring Framework")
    class SpringExceptionsTests {

        @Test
        @DisplayName("Deve tratar MethodArgumentNotValidException com campos de erro")
        void deveRetornar400ComDetalhesDeValidacao() {
            // Arrange
            BindingResult bindingResult = mock(BindingResult.class);
            MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
            
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError("veiculoDTO", "placa", "ABC1234", false, null, null, "Placa invï¿½lida"));
            fieldErrors.add(new FieldError("veiculoDTO", "ano", 1800, false, null, null, "Ano deve ser maior que 1900"));
            
            when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
            when(bindingResult.getErrorCount()).thenReturn(2);

            // Act
            ResponseEntity<ApiResponse<Void>> response = handler.handleMethodArgumentNotValid(exception, request);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            
            ApiResponse<Void> body = response.getBody();
            assertNotNull(body);
            assertFalse(body.isSuccess());
            assertEquals(400, body.getStatus());
            assertNotNull(body.getErrors());
            assertEquals(2, body.getErrors().size());
            
            // Valida detalhes do primeiro erro
            ApiResponse.ErrorDetail primeiroErro = body.getErrors().get(0);
            assertEquals("placa", primeiroErro.getField());
            assertEquals("Placa invï¿½lida", primeiroErro.getMessage());
            assertEquals("VALIDATION_ERROR", primeiroErro.getCode());
        }

        @Test
        @DisplayName("Deve tratar DataIntegrityViolationException com HTTP 409")
        void deveRetornar409QuandoViolacaoIntegridade() {
            // Arrange
            DataIntegrityViolationException exception = 
                new DataIntegrityViolationException("Constraint violation");

            // Act
            ResponseEntity<ApiResponse<Void>> response = handler.handleDataIntegrityViolation(exception, request);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            
            ApiResponse<Void> body = response.getBody();
            assertNotNull(body);
            assertFalse(body.isSuccess());
            assertEquals(409, body.getStatus());
            assertNotNull(body.getErrors());
            assertEquals("DATA_INTEGRITY_VIOLATION", body.getErrors().get(0).getCode());
        }

        @Test
        @DisplayName("Deve tratar PropertyReferenceException com HTTP 400")
        void deveRetornar400QuandoPropriedadeInvalida() {
            // Arrange
            // PropertyReferenceException requer um tipo vÃ¡lido, entÃ£o usamos mock
            PropertyReferenceException exception = mock(PropertyReferenceException.class);
            when(exception.getPropertyName()).thenReturn("nomeInvalido");

            // Act
            ResponseEntity<ApiResponse<Void>> response = handler.handlePropertyReference(exception, request);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            
            ApiResponse<Void> body = response.getBody();
            assertNotNull(body);
            assertFalse(body.isSuccess());
            assertEquals(400, body.getStatus());
            assertTrue(body.getMessage().contains("nomeInvalido"));
            assertNotNull(body.getErrors());
            assertEquals("INVALID_PROPERTY", body.getErrors().get(0).getCode());
        }

        @Test
        @DisplayName("Deve tratar IllegalArgumentException com HTTP 400")
        void deveRetornar400QuandoArgumentoIlegal() {
            // Arrange
            IllegalArgumentException exception = new IllegalArgumentException("Argumento invï¿½lido fornecido");

            // Act
            ResponseEntity<ApiResponse<Void>> response = handler.handleIllegalArgument(exception, request);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            
            ApiResponse<Void> body = response.getBody();
            assertNotNull(body);
            assertFalse(body.isSuccess());
            assertEquals(400, body.getStatus());
            assertEquals("Argumento invï¿½lido fornecido", body.getMessage());
            assertNotNull(body.getErrors());
            assertEquals("INVALID_ARGUMENT", body.getErrors().get(0).getCode());
        }
    }

    @Nested
    @DisplayName("Testes para Handler Genï¿½rico")
    class GenericExceptionTests {

        @Test
        @DisplayName("Deve tratar exceï¿½ï¿½o genï¿½rica nï¿½o prevista com HTTP 500")
        void deveRetornar500ParaExcecaoGenerica() {
            // Arrange
            Exception exception = new RuntimeException("Erro inesperado no sistema");

            // Act
            ResponseEntity<ApiResponse<Void>> response = handler.handleGenericException(exception, request);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            
            ApiResponse<Void> body = response.getBody();
            assertNotNull(body);
            assertFalse(body.isSuccess());
            assertEquals(500, body.getStatus());
            assertTrue(body.getMessage().contains("Erro interno"));
            assertNotNull(body.getErrors());
            assertEquals("INTERNAL_SERVER_ERROR", body.getErrors().get(0).getCode());
        }

        @Test
        @DisplayName("Deve incluir path da requisiï¿½ï¿½o em todas as respostas de erro")
        void deveIncluirPathEmTodasRespostas() {
            // Arrange
            when(request.getRequestURI()).thenReturn("/api/fabricantes/999");
            ResourceNotFoundException exception = new ResourceNotFoundException("Fabricante", 999L);

            // Act
            ResponseEntity<ApiResponse<Void>> response = handler.handleResourceNotFound(exception, request);

            // Assert
            ApiResponse<Void> body = response.getBody();
            assertNotNull(body);
            assertEquals("/api/fabricantes/999", body.getPath());
        }

        @Test
        @DisplayName("Deve incluir timestamp em todas as respostas de erro")
        void deveIncluirTimestampEmTodasRespostas() {
            // Arrange
            ResourceNotFoundException exception = new ResourceNotFoundException("Modelo", 1L);

            // Act
            ResponseEntity<ApiResponse<Void>> response = handler.handleResourceNotFound(exception, request);

            // Assert
            ApiResponse<Void> body = response.getBody();
            assertNotNull(body);
            assertNotNull(body.getTimestamp());
            assertFalse(body.getTimestamp().isEmpty());
        }

        @Test
        @DisplayName("Deve sempre marcar success=false em respostas de erro")
        void deveMarcarSuccessFalseEmErros() {
            // Arrange
            BusinessRuleException exception = new BusinessRuleException("Operaï¿½ï¿½o nï¿½o permitida");

            // Act
            ResponseEntity<ApiResponse<Void>> response = handler.handleBusinessRule(exception, request);

            // Assert
            ApiResponse<Void> body = response.getBody();
            assertNotNull(body);
            assertFalse(body.isSuccess());
        }
    }
}
