package com.veiculos.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando um recurso solicitado não é encontrado no sistema.
 * Retorna HTTP 404 Not Found.
 * 
 * Esta exceção deve ser usada em operações de busca (GET, PUT, DELETE)
 * quando o recurso identificado não existe na base de dados.
 * 
 * Exemplos de uso:
 * - Fabricante não encontrado por ID
 * - Modelo não encontrado por ID
 * - Veículo não encontrado por placa
 * 
 * @author Sistema de Veículos
 * @version 1.0
 * @since 26/11/2025
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;
    
    /**
     * Construtor completo com nome do recurso, campo e valor.
     * 
     * @param resourceName Nome do recurso (ex: "Fabricante", "Modelo", "Veículo")
     * @param fieldName Nome do campo usado na busca (ex: "id", "placa", "nome")
     * @param fieldValue Valor do campo que não foi encontrado
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s não encontrado com %s: %s", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    /**
     * Construtor simplificado apenas com nome do recurso e ID.
     * 
     * @param resourceName Nome do recurso (ex: "Fabricante", "Modelo", "Veículo")
     * @param id ID do recurso não encontrado
     */
    public ResourceNotFoundException(String resourceName, Long id) {
        this(resourceName, "id", id);
    }
    
    /**
     * Construtor para mensagem customizada.
     * 
     * @param message Mensagem de erro personalizada
     */
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }
    
    /**
     * Retorna o nome do recurso que não foi encontrado.
     * 
     * @return Nome do recurso
     */
    public String getResourceName() {
        return resourceName;
    }
    
    /**
     * Retorna o nome do campo usado na busca.
     * 
     * @return Nome do campo
     */
    public String getFieldName() {
        return fieldName;
    }
    
    /**
     * Retorna o valor do campo que não foi encontrado.
     * 
     * @return Valor do campo
     */
    public Object getFieldValue() {
        return fieldValue;
    }
}
