package com.veiculos.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando há violação de regras de negócio.
 * Retorna HTTP 422 Unprocessable Entity.
 * 
 * Esta exceção é usada quando os dados estão sintaticamente corretos,
 * mas violam regras de negócio específicas da aplicação.
 * 
 * Diferença entre ValidationException (400) e BusinessRuleException (422):
 * - 400: Dados malformados ou com formato inválido
 * - 422: Dados válidos mas violam lógica de negócio
 * 
 * Exemplos de uso:
 * - Tentar excluir fabricante que possui modelos associados
 * - Tentar excluir modelo que possui veículos cadastrados
 * - Operação não permitida no estado atual do recurso
 * - Violação de regras de integridade referencial
 * 
 * @author Sistema de Veículos
 * @version 1.0
 * @since 26/11/2025
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class BusinessRuleException extends RuntimeException {
    
    private final String ruleCode;
    private final String resourceName;
    
    /**
     * Construtor completo com código da regra e nome do recurso.
     * 
     * @param message Mensagem descritiva da violação
     * @param ruleCode Código identificador da regra violada
     * @param resourceName Nome do recurso envolvido
     */
    public BusinessRuleException(String message, String ruleCode, String resourceName) {
        super(message);
        this.ruleCode = ruleCode;
        this.resourceName = resourceName;
    }
    
    /**
     * Construtor com mensagem e código da regra.
     * 
     * @param message Mensagem descritiva da violação
     * @param ruleCode Código identificador da regra violada
     */
    public BusinessRuleException(String message, String ruleCode) {
        super(message);
        this.ruleCode = ruleCode;
        this.resourceName = null;
    }
    
    /**
     * Construtor simples apenas com mensagem.
     * 
     * @param message Mensagem descritiva da violação
     */
    public BusinessRuleException(String message) {
        super(message);
        this.ruleCode = null;
        this.resourceName = null;
    }
    
    /**
     * Factory method para violação de integridade referencial.
     * 
     * @param resourceName Nome do recurso (ex: "Fabricante", "Modelo")
     * @param dependentResource Nome do recurso dependente (ex: "Modelos", "Veículos")
     * @return BusinessRuleException configurada
     */
    public static BusinessRuleException cannotDeleteWithDependents(String resourceName, String dependentResource) {
        String message = String.format(
            "Não é possível excluir %s. Existem %s associados a ele.",
            resourceName,
            dependentResource
        );
        return new BusinessRuleException(message, "REFERENTIAL_INTEGRITY_VIOLATION", resourceName);
    }
    
    /**
     * Factory method para operação não permitida no estado atual.
     * 
     * @param operation Nome da operação (ex: "excluir", "atualizar")
     * @param resourceName Nome do recurso
     * @param reason Motivo da restrição
     * @return BusinessRuleException configurada
     */
    public static BusinessRuleException operationNotAllowed(String operation, String resourceName, String reason) {
        String message = String.format(
            "Não é possível %s %s. %s",
            operation,
            resourceName,
            reason
        );
        return new BusinessRuleException(message, "OPERATION_NOT_ALLOWED", resourceName);
    }
    
    /**
     * Retorna o código da regra violada.
     * 
     * @return Código da regra
     */
    public String getRuleCode() {
        return ruleCode;
    }
    
    /**
     * Retorna o nome do recurso envolvido.
     * 
     * @return Nome do recurso
     */
    public String getResourceName() {
        return resourceName;
    }
}
