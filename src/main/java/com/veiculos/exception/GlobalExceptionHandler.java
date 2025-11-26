package com.veiculos.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    //responsavel por formatar o corpo padrao da resposta de erro
    private ResponseEntity<Object> body(HttpStatus status, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", Instant.now().toString());
        map.put("status", status.value());
        map.put("error", status.getReasonPhrase());
        map.put("message", (message == null || message.isBlank()) ? defaultMessage(status) : message);
        return ResponseEntity.status(status).body(map);
    }

    private String defaultMessage(HttpStatus status) {
        return switch (status) {
            case NOT_FOUND -> "Recurso não encontrado";
            case BAD_REQUEST -> "Requisição inválida";
            case CONFLICT -> "Conflito de dados";
            default -> "Erro interno";
        };
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage();
        if (msg != null) {
            String lower = msg.toLowerCase();
            if (lower.contains("não encontrado")) {
                //log.debug("Recurso nao encontrado: {}", msg);
                return body(HttpStatus.NOT_FOUND, msg); //404
            }
            // Tratamento para violacao de integridade referencial
            if (lower.contains("veículos associados") || 
                lower.contains("não é possível excluir") ||
                lower.contains("existem veículos associados")) {
                return body(HttpStatus.CONFLICT, msg); //409
            }
        }
        //log.error("Erro inesperado", ex);
        return body(HttpStatus.INTERNAL_SERVER_ERROR, msg); //500
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        //modifica o status.code dependendo da mensagem
        String msg = ex.getMessage();
        String lower = msg == null ? "" : msg.toLowerCase();
        if (lower.contains("ja existe")) {
            return body(HttpStatus.CONFLICT, msg); //409
        }
        return body(HttpStatus.BAD_REQUEST, msg); //400
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrity(DataIntegrityViolationException ex) {
    //log.error("Violacao de integridade", ex);
        return body(HttpStatus.CONFLICT, "Violacao de integridade");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        return body(HttpStatus.BAD_REQUEST, "Dados invalidos");
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<Object> handlePropertyReference(PropertyReferenceException ex) {
        return body(HttpStatus.BAD_REQUEST, "Propriedade de ordenacao invalida: " + ex.getPropertyName());
    }
}
