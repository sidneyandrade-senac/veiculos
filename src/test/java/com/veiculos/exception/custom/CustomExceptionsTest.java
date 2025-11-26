package com.veiculos.exception.custom;

import com.veiculos.response.ApiResponse.ErrorDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para as exceções customizadas.
 * 
 * @author Sistema de Veículos
 * @version 1.0
 * @since 26/11/2025
 */
@DisplayName("Testes das Exceções Customizadas")
class CustomExceptionsTest {

    @Nested
    @DisplayName("ResourceNotFoundException")
    class ResourceNotFoundExceptionTest {

        @Test
        @DisplayName("Deve criar exceção com todos os parâmetros")
        void deveCriarComTodosParametros() {
            // Act
            ResourceNotFoundException ex = new ResourceNotFoundException("Fabricante", "id", 1L);
            
            // Assert
            assertEquals("Fabricante não encontrado com id: 1", ex.getMessage());
            assertEquals("Fabricante", ex.getResourceName());
            assertEquals("id", ex.getFieldName());
            assertEquals(1L, ex.getFieldValue());
        }

        @Test
        @DisplayName("Deve criar exceção com nome e ID")
        void deveCriarComNomeEId() {
            // Act
            ResourceNotFoundException ex = new ResourceNotFoundException("Modelo", 5L);
            
            // Assert
            assertEquals("Modelo não encontrado com id: 5", ex.getMessage());
            assertEquals("Modelo", ex.getResourceName());
            assertEquals("id", ex.getFieldName());
            assertEquals(5L, ex.getFieldValue());
        }

        @Test
        @DisplayName("Deve criar exceção com mensagem customizada")
        void deveCriarComMensagemCustomizada() {
            // Act
            ResourceNotFoundException ex = new ResourceNotFoundException("Veículo não encontrado com placa ABC1234");
            
            // Assert
            assertEquals("Veículo não encontrado com placa ABC1234", ex.getMessage());
            assertNull(ex.getResourceName());
            assertNull(ex.getFieldName());
            assertNull(ex.getFieldValue());
        }

        @Test
        @DisplayName("Deve ter anotação @ResponseStatus com NOT_FOUND")
        void deveTerResponseStatusNotFound() {
            // Act
            ResponseStatus annotation = ResourceNotFoundException.class.getAnnotation(ResponseStatus.class);
            
            // Assert
            assertNotNull(annotation);
            assertEquals(HttpStatus.NOT_FOUND, annotation.value());
        }
    }

    @Nested
    @DisplayName("ResourceAlreadyExistsException")
    class ResourceAlreadyExistsExceptionTest {

        @Test
        @DisplayName("Deve criar exceção com todos os parâmetros")
        void deveCriarComTodosParametros() {
            // Act
            ResourceAlreadyExistsException ex = new ResourceAlreadyExistsException("Fabricante", "nome", "Toyota");
            
            // Assert
            assertEquals("Fabricante já existe com nome: Toyota", ex.getMessage());
            assertEquals("Fabricante", ex.getResourceName());
            assertEquals("nome", ex.getFieldName());
            assertEquals("Toyota", ex.getFieldValue());
        }

        @Test
        @DisplayName("Deve criar exceção com nome do recurso e valor")
        void deveCriarComNomeEValor() {
            // Act
            ResourceAlreadyExistsException ex = new ResourceAlreadyExistsException("Modelo", "Corolla");
            
            // Assert
            assertEquals("Modelo já existe com nome: Corolla", ex.getMessage());
            assertEquals("Modelo", ex.getResourceName());
            assertEquals("nome", ex.getFieldName());
            assertEquals("Corolla", ex.getFieldValue());
        }

        @Test
        @DisplayName("Deve criar exceção com mensagem customizada")
        void deveCriarComMensagemCustomizada() {
            // Act
            ResourceAlreadyExistsException ex = new ResourceAlreadyExistsException("Placa ABC1234 já cadastrada");
            
            // Assert
            assertEquals("Placa ABC1234 já cadastrada", ex.getMessage());
            assertNull(ex.getResourceName());
            assertNull(ex.getFieldName());
            assertNull(ex.getFieldValue());
        }

        @Test
        @DisplayName("Deve ter anotação @ResponseStatus com CONFLICT")
        void deveTerResponseStatusConflict() {
            // Act
            ResponseStatus annotation = ResourceAlreadyExistsException.class.getAnnotation(ResponseStatus.class);
            
            // Assert
            assertNotNull(annotation);
            assertEquals(HttpStatus.CONFLICT, annotation.value());
        }
    }

    @Nested
    @DisplayName("ValidationException")
    class ValidationExceptionTest {

        @Test
        @DisplayName("Deve criar exceção com lista de erros")
        void deveCriarComListaDeErros() {
            // Arrange
            List<ErrorDetail> errors = Arrays.asList(
                ErrorDetail.fieldError("VALIDATION_ERROR", "nome", "Nome é obrigatório"),
                ErrorDetail.fieldError("VALIDATION_ERROR", "placa", "Placa inválida")
            );
            
            // Act
            ValidationException ex = new ValidationException("Erro de validação", errors);
            
            // Assert
            assertEquals("Erro de validação", ex.getMessage());
            assertEquals(2, ex.getErrorCount());
            assertTrue(ex.hasErrors());
            assertEquals("nome", ex.getErrors().get(0).getField());
            assertEquals("placa", ex.getErrors().get(1).getField());
        }

        @Test
        @DisplayName("Deve criar exceção com mensagem simples")
        void deveCriarComMensagemSimples() {
            // Act
            ValidationException ex = new ValidationException("Dados inválidos");
            
            // Assert
            assertEquals("Dados inválidos", ex.getMessage());
            assertEquals(0, ex.getErrorCount());
            assertFalse(ex.hasErrors());
        }

        @Test
        @DisplayName("Deve criar exceção para campo único")
        void deveCriarParaCampoUnico() {
            // Act
            ValidationException ex = new ValidationException("placa", "Placa deve ter 7 caracteres");
            
            // Assert
            assertTrue(ex.getMessage().contains("placa"));
            assertTrue(ex.getMessage().contains("Placa deve ter 7 caracteres"));
            assertEquals(1, ex.getErrorCount());
            assertEquals("placa", ex.getErrors().get(0).getField());
        }

        @Test
        @DisplayName("Deve criar exceção com valor rejeitado")
        void deveCriarComValorRejeitado() {
            // Act
            ValidationException ex = new ValidationException("ano", "Ano inválido", 1800);
            
            // Assert
            assertTrue(ex.getMessage().contains("ano"));
            assertTrue(ex.getMessage().contains("1800"));
            assertEquals(1, ex.getErrorCount());
            assertEquals("ano", ex.getErrors().get(0).getField());
            assertEquals(1800, ex.getErrors().get(0).getRejectedValue());
        }

        @Test
        @DisplayName("Deve adicionar erros fluentemente")
        void deveAdicionarErrosFluentemente() {
            // Arrange
            ValidationException ex = new ValidationException("Erro de validação");
            
            // Act
            ex.addFieldError("nome", "Nome obrigatório")
              .addFieldError("placa", "Placa inválida")
              .addError(ErrorDetail.fieldError("VALIDATION_ERROR", "ano", "Ano inválido", 1800));
            
            // Assert
            assertEquals(3, ex.getErrorCount());
            assertTrue(ex.hasErrors());
        }

        @Test
        @DisplayName("Deve ter anotação @ResponseStatus com BAD_REQUEST")
        void deveTerResponseStatusBadRequest() {
            // Act
            ResponseStatus annotation = ValidationException.class.getAnnotation(ResponseStatus.class);
            
            // Assert
            assertNotNull(annotation);
            assertEquals(HttpStatus.BAD_REQUEST, annotation.value());
        }
    }

    @Nested
    @DisplayName("BusinessRuleException")
    class BusinessRuleExceptionTest {

        @Test
        @DisplayName("Deve criar exceção com todos os parâmetros")
        void deveCriarComTodosParametros() {
            // Act
            BusinessRuleException ex = new BusinessRuleException(
                "Não é possível excluir fabricante com modelos",
                "REFERENTIAL_INTEGRITY",
                "Fabricante"
            );
            
            // Assert
            assertEquals("Não é possível excluir fabricante com modelos", ex.getMessage());
            assertEquals("REFERENTIAL_INTEGRITY", ex.getRuleCode());
            assertEquals("Fabricante", ex.getResourceName());
        }

        @Test
        @DisplayName("Deve criar exceção com mensagem e código")
        void deveCriarComMensagemECodigo() {
            // Act
            BusinessRuleException ex = new BusinessRuleException(
                "Operação não permitida",
                "OPERATION_NOT_ALLOWED"
            );
            
            // Assert
            assertEquals("Operação não permitida", ex.getMessage());
            assertEquals("OPERATION_NOT_ALLOWED", ex.getRuleCode());
            assertNull(ex.getResourceName());
        }

        @Test
        @DisplayName("Deve criar exceção com mensagem simples")
        void deveCriarComMensagemSimples() {
            // Act
            BusinessRuleException ex = new BusinessRuleException("Regra de negócio violada");
            
            // Assert
            assertEquals("Regra de negócio violada", ex.getMessage());
            assertNull(ex.getRuleCode());
            assertNull(ex.getResourceName());
        }

        @Test
        @DisplayName("Deve criar exceção com factory method cannotDeleteWithDependents")
        void deveCriarComFactoryMethodCannotDelete() {
            // Act
            BusinessRuleException ex = BusinessRuleException.cannotDeleteWithDependents(
                "Fabricante",
                "Modelos"
            );
            
            // Assert
            assertTrue(ex.getMessage().contains("Não é possível excluir Fabricante"));
            assertTrue(ex.getMessage().contains("Modelos associados"));
            assertEquals("REFERENTIAL_INTEGRITY_VIOLATION", ex.getRuleCode());
            assertEquals("Fabricante", ex.getResourceName());
        }

        @Test
        @DisplayName("Deve criar exceção com factory method operationNotAllowed")
        void deveCriarComFactoryMethodOperationNotAllowed() {
            // Act
            BusinessRuleException ex = BusinessRuleException.operationNotAllowed(
                "atualizar",
                "Veículo",
                "Veículo está em processo de venda"
            );
            
            // Assert
            assertTrue(ex.getMessage().contains("Não é possível atualizar Veículo"));
            assertTrue(ex.getMessage().contains("processo de venda"));
            assertEquals("OPERATION_NOT_ALLOWED", ex.getRuleCode());
            assertEquals("Veículo", ex.getResourceName());
        }

        @Test
        @DisplayName("Deve ter anotação @ResponseStatus com UNPROCESSABLE_ENTITY")
        void deveTerResponseStatusUnprocessableEntity() {
            // Act
            ResponseStatus annotation = BusinessRuleException.class.getAnnotation(ResponseStatus.class);
            
            // Assert
            assertNotNull(annotation);
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, annotation.value());
        }
    }
}
