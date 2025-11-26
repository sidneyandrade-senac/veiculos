package com.veiculos.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando há tentativa de criar um recurso que já existe no sistema.
 * Retorna HTTP 409 Conflict.
 * 
 * Esta exceção deve ser usada em operações de criação (POST) ou atualização (PUT)
 * quando há violação de constraint de unicidade (ex: nome único, placa única).
 * 
 * Exemplos de uso:
 * - Tentar criar fabricante com nome já existente
 * - Tentar criar modelo com nome já cadastrado
 * - Tentar cadastrar veículo com placa duplicada
 * 
 * @author Sistema de Veículos
 * @version 1.0
 * @since 26/11/2025
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends RuntimeException {
    
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;
    
    /**
     * Construtor completo com nome do recurso, campo e valor duplicado.
     * 
     * @param resourceName Nome do recurso (ex: "Fabricante", "Modelo", "Veículo")
     * @param fieldName Nome do campo duplicado (ex: "nome", "placa")
     * @param fieldValue Valor do campo que já existe
     */
    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s já existe com %s: %s", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    /**
     * Construtor simplificado para duplicata de nome.
     * 
     * @param resourceName Nome do recurso (ex: "Fabricante", "Modelo")
     * @param nome Nome duplicado
     */
    public ResourceAlreadyExistsException(String resourceName, String nome) {
        this(resourceName, "nome", nome);
    }
    
    /**
     * Construtor para mensagem customizada.
     * 
     * @param message Mensagem de erro personalizada
     */
    public ResourceAlreadyExistsException(String message) {
        super(message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }
    
    /**
     * Retorna o nome do recurso que já existe.
     * 
     * @return Nome do recurso
     */
    public String getResourceName() {
        return resourceName;
    }
    
    /**
     * Retorna o nome do campo duplicado.
     * 
     * @return Nome do campo
     */
    public String getFieldName() {
        return fieldName;
    }
    
    /**
     * Retorna o valor do campo duplicado.
     * 
     * @return Valor do campo
     */
    public Object getFieldValue() {
        return fieldValue;
    }
}
