package com.veiculos.exception.custom;

import com.veiculos.response.ApiResponse.ErrorDetail;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Exceção lançada quando há erros de validação em dados de entrada.
 * Retorna HTTP 400 Bad Request.
 * 
 * Esta exceção suporta múltiplos erros de validação, permitindo
 * informar ao cliente todos os problemas encontrados de uma vez.
 * 
 * Exemplos de uso:
 * - Campos obrigatórios não preenchidos
 * - Formatos inválidos (ex: placa, ano)
 * - Valores fora do range permitido
 * - Validações customizadas de negócio
 * 
 * @author Sistema de Veículos
 * @version 1.0
 * @since 26/11/2025
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {
    
    private final List<ErrorDetail> errors;
    
    /**
     * Construtor com lista de erros de validação.
     * 
     * @param message Mensagem geral do erro
     * @param errors Lista de detalhes dos erros encontrados
     */
    public ValidationException(String message, List<ErrorDetail> errors) {
        super(message);
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
    }
    
    /**
     * Construtor com mensagem simples (sem lista de erros).
     * 
     * @param message Mensagem de erro de validação
     */
    public ValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
    }
    
    /**
     * Construtor para erro de validação de campo único.
     * 
     * @param fieldName Nome do campo com erro
     * @param message Mensagem de erro
     */
    public ValidationException(String fieldName, String message) {
        super(String.format("Erro de validação no campo %s: %s", fieldName, message));
        this.errors = new ArrayList<>();
        this.errors.add(ErrorDetail.fieldError("VALIDATION_ERROR", fieldName, message));
    }
    
    /**
     * Construtor para erro de validação de campo com valor rejeitado.
     * 
     * @param fieldName Nome do campo com erro
     * @param message Mensagem de erro
     * @param rejectedValue Valor que foi rejeitado
     */
    public ValidationException(String fieldName, String message, Object rejectedValue) {
        super(String.format("Erro de validação no campo %s: %s (valor: %s)", fieldName, message, rejectedValue));
        this.errors = new ArrayList<>();
        this.errors.add(ErrorDetail.fieldError("VALIDATION_ERROR", fieldName, message, rejectedValue));
    }
    
    /**
     * Retorna a lista de erros de validação.
     * 
     * @return Lista imutável de erros
     */
    public List<ErrorDetail> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * Adiciona um erro de validação à lista.
     * 
     * @param error Detalhe do erro a adicionar
     * @return Esta instância (para encadeamento)
     */
    public ValidationException addError(ErrorDetail error) {
        this.errors.add(error);
        return this;
    }
    
    /**
     * Adiciona um erro de validação de campo.
     * 
     * @param fieldName Nome do campo
     * @param message Mensagem de erro
     * @return Esta instância (para encadeamento)
     */
    public ValidationException addFieldError(String fieldName, String message) {
        this.errors.add(ErrorDetail.fieldError("VALIDATION_ERROR", fieldName, message));
        return this;
    }
    
    /**
     * Verifica se há erros de validação.
     * 
     * @return true se houver erros, false caso contrário
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Retorna o número de erros de validação.
     * 
     * @return Quantidade de erros
     */
    public int getErrorCount() {
        return errors.size();
    }
}
