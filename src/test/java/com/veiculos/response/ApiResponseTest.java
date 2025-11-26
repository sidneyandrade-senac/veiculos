package com.veiculos.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe ApiResponse.
 * 
 * @author Sistema de Veículos
 * @version 1.0
 * @since 26/11/2025
 */
@DisplayName("Testes da classe ApiResponse")
class ApiResponseTest {

    @Test
    @DisplayName("Deve criar resposta de sucesso com todos os campos")
    void deveCriarRespostaSucessoCompleta() {
        // Arrange
        String data = "Teste de dados";
        String message = "Operação realizada com sucesso";
        int status = 200;
        
        // Act
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .status(status)
                .message(message)
                .data(data)
                .timestamp("2025-11-26T10:30:00Z")
                .path("/api/v1/teste")
                .build();
        
        // Assert
        assertTrue(response.isSuccess());
        assertEquals(status, response.getStatus());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertNotNull(response.getTimestamp());
        assertEquals("/api/v1/teste", response.getPath());
        assertNull(response.getErrors());
        assertNull(response.getMeta());
    }

    @Test
    @DisplayName("Deve criar resposta de sucesso usando método estático success()")
    void deveCriarRespostaSucessoMetodoEstatico() {
        // Arrange
        String data = "Dados de teste";
        
        // Act
        ApiResponse<String> response = ApiResponse.success(data);
        
        // Assert
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals("Operação realizada com sucesso", response.getMessage());
        assertEquals(data, response.getData());
        assertNotNull(response.getTimestamp());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Deve criar resposta de sucesso com código 201 Created")
    void deveCriarRespostaSucessoCreated() {
        // Arrange
        String data = "Recurso criado";
        String message = "Fabricante criado com sucesso";
        
        // Act
        ApiResponse<String> response = ApiResponse.created(data, message);
        
        // Assert
        assertTrue(response.isSuccess());
        assertEquals(201, response.getStatus());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Deve criar resposta de erro com lista de detalhes")
    void deveCriarRespostaErroComDetalhes() {
        // Arrange
        String message = "Erro de validação";
        int status = 400;
        List<ApiResponse.ErrorDetail> errors = Arrays.asList(
            ApiResponse.ErrorDetail.builder()
                .code("VALIDATION_ERROR")
                .field("nome")
                .message("Nome é obrigatório")
                .build(),
            ApiResponse.ErrorDetail.builder()
                .code("VALIDATION_ERROR")
                .field("placa")
                .message("Placa inválida")
                .build()
        );
        
        // Act
        ApiResponse<Void> response = ApiResponse.error(message, status, errors);
        
        // Assert
        assertFalse(response.isSuccess());
        assertEquals(status, response.getStatus());
        assertEquals(message, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
        assertEquals(2, response.getErrors().size());
        assertEquals("nome", response.getErrors().get(0).getField());
        assertEquals("placa", response.getErrors().get(1).getField());
    }

    @Test
    @DisplayName("Deve criar resposta de erro simples")
    void deveCriarRespostaErroSimples() {
        // Arrange
        String message = "Recurso não encontrado";
        int status = 404;
        
        // Act
        ApiResponse<Void> response = ApiResponse.error(message, status);
        
        // Assert
        assertFalse(response.isSuccess());
        assertEquals(status, response.getStatus());
        assertEquals(message, response.getMessage());
        assertNull(response.getData());
        assertNull(response.getErrors());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Deve adicionar metadados à resposta")
    void deveAdicionarMetadados() {
        // Arrange
        ApiResponse<String> response = ApiResponse.success("Dados");
        
        // Act
        response.addMeta("total", 100);
        response.addMeta("page", 1);
        response.addMeta("size", 10);
        
        // Assert
        assertNotNull(response.getMeta());
        assertEquals(3, response.getMeta().size());
        assertEquals(100, response.getMeta().get("total"));
        assertEquals(1, response.getMeta().get("page"));
        assertEquals(10, response.getMeta().get("size"));
    }

    @Test
    @DisplayName("Deve adicionar path Ã  resposta usando withPath()")
    void deveAdicionarPath() {
        // Arrange
        String path = "/api/v1/fabricantes/1";
        ApiResponse<String> response = ApiResponse.success("Dados");
        
        // Act
        ApiResponse<String> result = response.withPath(path);
        
        // Assert
        assertSame(response, result); // Verifica fluent interface
        assertEquals(path, response.getPath());
    }

    @Test
    @DisplayName("Deve criar ErrorDetail simples")
    void deveCriarErrorDetailSimples() {
        // Arrange & Act
        ApiResponse.ErrorDetail error = ApiResponse.ErrorDetail.of(
            "RESOURCE_NOT_FOUND",
            "Fabricante não encontrado"
        );
        
        // Assert
        assertEquals("RESOURCE_NOT_FOUND", error.getCode());
        assertEquals("Fabricante não encontrado", error.getMessage());
        assertNull(error.getField());
        assertNull(error.getRejectedValue());
    }

    @Test
    @DisplayName("Deve criar ErrorDetail de campo")
    void deveCriarErrorDetailDeCampo() {
        // Arrange & Act
        ApiResponse.ErrorDetail error = ApiResponse.ErrorDetail.fieldError(
            "VALIDATION_ERROR",
            "placa",
            "Placa deve ter 7 caracteres"
        );
        
        // Assert
        assertEquals("VALIDATION_ERROR", error.getCode());
        assertEquals("placa", error.getField());
        assertEquals("Placa deve ter 7 caracteres", error.getMessage());
        assertNull(error.getRejectedValue());
    }

    @Test
    @DisplayName("Deve criar ErrorDetail com valor rejeitado")
    void deveCriarErrorDetailComValorRejeitado() {
        // Arrange & Act
        ApiResponse.ErrorDetail error = ApiResponse.ErrorDetail.fieldError(
            "VALIDATION_ERROR",
            "ano",
            "Ano deve estar entre 1886 e 2026",
            1800
        );
        
        // Assert
        assertEquals("VALIDATION_ERROR", error.getCode());
        assertEquals("ano", error.getField());
        assertEquals("Ano deve estar entre 1886 e 2026", error.getMessage());
        assertEquals(1800, error.getRejectedValue());
    }

    @Test
    @DisplayName("Deve criar resposta com tipo genérico complexo")
    void deveCriarRespostaComTipoGenericoComplexo() {
        // Arrange
        List<String> data = Arrays.asList("Item 1", "Item 2", "Item 3");
        
        // Act
        ApiResponse<List<String>> response = ApiResponse.success(data, "Lista recuperada", 200);
        
        // Assert
        assertTrue(response.isSuccess());
        assertEquals(3, response.getData().size());
        assertEquals("Item 1", response.getData().get(0));
    }

    @Test
    @DisplayName("Deve encadear métodos fluentemente")
    void deveEncadearMetodosFluentemente() {
        // Arrange & Act
        ApiResponse<String> response = ApiResponse.success("Dados")
                .withPath("/api/v1/teste")
                .addMeta("version", "1.0")
                .addMeta("requestId", "123-456");
        
        // Assert
        assertEquals("/api/v1/teste", response.getPath());
        assertEquals("1.0", response.getMeta().get("version"));
        assertEquals("123-456", response.getMeta().get("requestId"));
    }

    @Test
    @DisplayName("Deve permitir ErrorDetail com Builder completo")
    void devePermitirErrorDetailComBuilderCompleto() {
        // Arrange & Act
        ApiResponse.ErrorDetail error = ApiResponse.ErrorDetail.builder()
                .code("CUSTOM_ERROR")
                .field("customField")
                .message("Mensagem customizada")
                .rejectedValue("valor_invalido")
                .build();
        
        // Assert
        assertEquals("CUSTOM_ERROR", error.getCode());
        assertEquals("customField", error.getField());
        assertEquals("Mensagem customizada", error.getMessage());
        assertEquals("valor_invalido", error.getRejectedValue());
    }

    @Test
    @DisplayName("Deve suportar múltiplas adições de metadados")
    void deveSuportarMultiplasAdicoesMetadados() {
        // Arrange
        ApiResponse<String> response = ApiResponse.success("Dados");
        
        // Act
        response.addMeta("key1", "value1");
        response.addMeta("key2", 123);
        response.addMeta("key3", true);
        
        // Assert
        assertEquals(3, response.getMeta().size());
        assertEquals("value1", response.getMeta().get("key1"));
        assertEquals(123, response.getMeta().get("key2"));
        assertEquals(true, response.getMeta().get("key3"));
    }
}


