package com.veiculos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Classe genérica para padronização de respostas da API REST.
 * 
 * Esta classe encapsula todas as respostas da API, garantindo consistência
 * entre respostas de sucesso e erro, facilitando o tratamento no frontend
 * e melhorando a experiência do desenvolvedor.
 * 
 * Padrão de uso:
 * - Respostas de sucesso: success=true, data preenchido, errors=null
 * - Respostas de erro: success=false, data=null, errors preenchido
 * 
 * @param <T> Tipo do dado retornado (payload)
 * 
 * @author Sistema de Veículos
 * @version 1.0
 * @since 26/11/2025
 */
@Getter
@Setter
@Builder
public class ApiResponse<T> {
    
    /**
     * Indica se a operação foi bem-sucedida.
     * true = sucesso, false = erro
     */
    private boolean success;
    
    /**
     * Código de status HTTP da resposta.
     * Ex: 200, 201, 400, 404, 500
     */
    private int status;
    
    /**
     * Mensagem descritiva da operação.
     * Deve ser amigável para exibição ao usuário final.
     */
    private String message;
    
    /**
     * Payload da resposta (dados retornados).
     * Pode ser um objeto, lista, ou qualquer tipo genérico.
     * Null em caso de erro.
     */
    private T data;
    
    /**
     * Timestamp da resposta no formato ISO-8601.
     * Ex: "2025-11-26T10:30:00.000Z"
     */
    private String timestamp;
    
    /**
     * Path do endpoint que gerou a resposta.
     * Ex: "/api/v1/fabricantes/1"
     */
    private String path;
    
    /**
     * Lista de detalhes de erros (usado em validações e erros múltiplos).
     * Null em caso de sucesso.
     */
    private List<ErrorDetail> errors;
    
    /**
     * Metadados adicionais da resposta (opcional).
     * Útil para paginação, informações extras, links, etc.
     * Ex: {"total": 100, "page": 1, "size": 10}
     */
    private Map<String, Object> meta;
    
    /**
     * Cria uma resposta de sucesso com dados.
     * 
     * @param <T> Tipo do dado
     * @param data Dados a serem retornados
     * @param message Mensagem de sucesso
     * @param status Código HTTP (geralmente 200 ou 201)
     * @return ApiResponse de sucesso
     */
    public static <T> ApiResponse<T> success(T data, String message, int status) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(status)
                .message(message)
                .data(data)
                .timestamp(Instant.now().toString())
                .build();
    }
    
    /**
     * Cria uma resposta de sucesso simples (200 OK).
     * 
     * @param <T> Tipo do dado
     * @param data Dados a serem retornados
     * @return ApiResponse de sucesso
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operação realizada com sucesso", 200);
    }
    
    /**
     * Cria uma resposta de sucesso com código 201 Created.
     * 
     * @param <T> Tipo do dado
     * @param data Dados criados
     * @param message Mensagem de sucesso
     * @return ApiResponse de sucesso
     */
    public static <T> ApiResponse<T> created(T data, String message) {
        return success(data, message, 201);
    }
    
    /**
     * Cria uma resposta de erro.
     * 
     * @param <T> Tipo do dado (geralmente Void)
     * @param message Mensagem de erro
     * @param status CÃ³digo HTTP de erro
     * @param errors Lista de detalhes do erro
     * @return ApiResponse de erro
     */
    public static <T> ApiResponse<T> error(String message, int status, List<ErrorDetail> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .timestamp(Instant.now().toString())
                .errors(errors)
                .build();
    }
    
    /**
     * Cria uma resposta de erro simples (sem lista de erros).
     * 
     * @param <T> Tipo do dado
     * @param message Mensagem de erro
     * @param status CÃ³digo HTTP de erro
     * @return ApiResponse de erro
     */
    public static <T> ApiResponse<T> error(String message, int status) {
        return error(message, status, null);
    }
    
    /**
     * Adiciona metadados à resposta.
     * 
     * @param key Chave do metadado
     * @param value Valor do metadado
     * @return Esta instÃ¢ncia (para encadeamento)
     */
    public ApiResponse<T> addMeta(String key, Object value) {
        if (this.meta == null) {
            this.meta = new java.util.HashMap<>();
        }
        this.meta.put(key, value);
        return this;
    }
    
    /**
     * Define o path da requisição.
     * 
     * @param path Path do endpoint
     * @return Esta instância (para encadeamento)
     */
    public ApiResponse<T> withPath(String path) {
        this.path = path;
        return this;
    }
    
    /**
     * Classe interna para representar detalhes de erros individuais.
     * Útil para validações que retornam múltiplos erros de campos.
     */
    @Getter
    @Setter
    @Builder
    public static class ErrorDetail {
        
        /**
         * Código único do erro para identificação programática.
         * Ex: "VALIDATION_ERROR", "RESOURCE_NOT_FOUND", "DUPLICATE_ENTRY"
         */
        private String code;
        
        /**
         * Nome do campo que gerou o erro (para erros de validação).
         * Ex: "placa", "nome", "ano"
         * Null para erros genéricos.
         */
        private String field;
        
        /**
         * Mensagem descritiva do erro.
         * Deve ser amigável para exibição ao usuário.
         */
        private String message;
        
        /**
         * Valor rejeitado (opcional).
         * Útil para debugging.
         */
        private Object rejectedValue;
        
        /**
         * Cria um detalhe de erro simples.
         * 
         * @param code CÃ³digo do erro
         * @param message Mensagem do erro
         * @return ErrorDetail
         */
        public static ErrorDetail of(String code, String message) {
            return ErrorDetail.builder()
                    .code(code)
                    .message(message)
                    .build();
        }
        
        /**
         * Cria um detalhe de erro de validação de campo.
         * 
         * @param code CÃ³digo do erro
         * @param field Nome do campo
         * @param message Mensagem do erro
         * @return ErrorDetail
         */
        public static ErrorDetail fieldError(String code, String field, String message) {
            return ErrorDetail.builder()
                    .code(code)
                    .field(field)
                    .message(message)
                    .build();
        }
        
        /**
         * Cria um detalhe de erro de validação com valor rejeitado.
         * 
         * @param code Código do erro
         * @param field Nome do campo
         * @param message Mensagem do erro
         * @param rejectedValue Valor que foi rejeitado
         * @return ErrorDetail
         */
        public static ErrorDetail fieldError(String code, String field, String message, Object rejectedValue) {
            return ErrorDetail.builder()
                    .code(code)
                    .field(field)
                    .message(message)
                    .rejectedValue(rejectedValue)
                    .build();
        }
    }
}


