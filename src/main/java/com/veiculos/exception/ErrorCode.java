package com.veiculos.exception;

/**
 * Codigos de erro padronizados para a API.
 * 
 * Cada codigo e unico e facilita a identificacao e tratamento de erros
 * especificos pelos clientes da API.
 * 
 * Formato: [CATEGORIA]_[DESCRICAO]
 * - Categorias: RESOURCE, VALIDATION, BUSINESS, DATA, SYSTEM
 * 
 * @author Sistema de Veiculos
 * @version 1.0
 * @since 26/11/2025
 */
public enum ErrorCode {
    
    // ========== RESOURCE ERRORS (404, 409) ==========
    
    /**
     * Recurso nao encontrado no sistema.
     * HTTP Status: 404 Not Found
     */
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "Recurso nao encontrado"),
    
    /**
     * Tentativa de criar recurso que ja existe (duplicacao).
     * HTTP Status: 409 Conflict
     */
    RESOURCE_ALREADY_EXISTS("RESOURCE_ALREADY_EXISTS", "Recurso ja existe"),
    
    // ========== VALIDATION ERRORS (400) ==========
    
    /**
     * Dados de entrada invalidos ou malformados.
     * HTTP Status: 400 Bad Request
     */
    VALIDATION_ERROR("VALIDATION_ERROR", "Erro de validacao"),
    
    /**
     * Argumento invalido passado para metodo/endpoint.
     * HTTP Status: 400 Bad Request
     */
    INVALID_ARGUMENT("INVALID_ARGUMENT", "Argumento invalido"),
    
    /**
     * Propriedade invalida utilizada para ordenacao/filtro.
     * HTTP Status: 400 Bad Request
     */
    INVALID_PROPERTY("INVALID_PROPERTY", "Propriedade invalida"),
    
    /**
     * Formato de dados invalido (ex: placa, data, etc).
     * HTTP Status: 400 Bad Request
     */
    INVALID_FORMAT("INVALID_FORMAT", "Formato invalido"),
    
    // ========== BUSINESS RULE ERRORS (422) ==========
    
    /**
     * Violacao de regra de negocio.
     * HTTP Status: 422 Unprocessable Entity
     */
    BUSINESS_RULE_VIOLATION("BUSINESS_RULE_VIOLATION", "Violacao de regra de negocio"),
    
    /**
     * Operacao nao permitida no estado atual do recurso.
     * HTTP Status: 422 Unprocessable Entity
     */
    OPERATION_NOT_ALLOWED("OPERATION_NOT_ALLOWED", "Operacao nao permitida"),
    
    /**
     * Tentativa de exclusao de recurso com dependencias.
     * HTTP Status: 422 Unprocessable Entity
     */
    CANNOT_DELETE_WITH_DEPENDENTS("CANNOT_DELETE_WITH_DEPENDENTS", "Nao e possivel excluir recurso com dependencias"),
    
    // ========== DATA INTEGRITY ERRORS (409) ==========
    
    /**
     * Violacao de constraint de integridade no banco de dados.
     * HTTP Status: 409 Conflict
     */
    DATA_INTEGRITY_VIOLATION("DATA_INTEGRITY_VIOLATION", "Violacao de integridade de dados"),
    
    /**
     * Violacao de constraint de unicidade (unique).
     * HTTP Status: 409 Conflict
     */
    UNIQUE_CONSTRAINT_VIOLATION("UNIQUE_CONSTRAINT_VIOLATION", "Violacao de restricao de unicidade"),
    
    /**
     * Violacao de constraint de chave estrangeira.
     * HTTP Status: 409 Conflict
     */
    FOREIGN_KEY_VIOLATION("FOREIGN_KEY_VIOLATION", "Violacao de integridade referencial"),
    
    // ========== SYSTEM ERRORS (500) ==========
    
    /**
     * Erro interno do servidor.
     * HTTP Status: 500 Internal Server Error
     */
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Erro interno do servidor"),
    
    /**
     * Erro inesperado nao tratado.
     * HTTP Status: 500 Internal Server Error
     */
    UNEXPECTED_ERROR("UNEXPECTED_ERROR", "Erro inesperado");
    
    private final String code;
    private final String defaultMessage;
    
    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
    
    /**
     * Retorna o codigo unico do erro.
     * 
     * @return codigo do erro
     */
    public String getCode() {
        return code;
    }
    
    /**
     * Retorna a mensagem padrao associada ao codigo de erro.
     * 
     * @return mensagem padrao
     */
    public String getDefaultMessage() {
        return defaultMessage;
    }
    
    @Override
    public String toString() {
        return code;
    }
}
