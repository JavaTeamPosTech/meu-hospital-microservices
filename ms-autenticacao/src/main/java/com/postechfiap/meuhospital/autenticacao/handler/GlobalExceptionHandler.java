package com.postechfiap.meuhospital.autenticacao.handler;

import com.postechfiap.meuhospital.autenticacao.exception.RecursoNaoEncontradoException;
import com.postechfiap.meuhospital.autenticacao.exception.UsuarioExistenteException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Tratador Global de Exceções (@ControllerAdvice) para centralizar a resposta a erros
 * de validação, integridade de dados e regras de domínio em formato JSON padronizado.
 */
@ControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Constrói uma resposta de erro padronizada.
     */
    private Map<String, Object> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", request.getRequestURI());
        return errorResponse;
    }

    /**
     * 1. Trata erros de validação (@Valid nos DTOs). Retorna 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("VALIDAÇÃO FALHOU: {} erros em {}.", errors.size(), request.getRequestURI());

        Map<String, Object> response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Erros de validação encontrados. Verifique os campos.",
                request
        );
        response.put("validationErrors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 2. Trata exceções de domínio (e-mail ou CPF duplicado). Retorna 409 Conflict.
     */
    @ExceptionHandler(UsuarioExistenteException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, Object>> handleUsuarioExistente(
            UsuarioExistenteException ex, HttpServletRequest request) {

        log.warn("CONFLITO (409): {}", ex.getMessage());

        Map<String, Object> response = buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * 3. Trata exceções de recurso não encontrado. Retorna 404 Not Found.
     */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex, HttpServletRequest request) {

        log.info("RECURSO NÃO ENCONTRADO (404): {}", ex.getMessage());

        Map<String, Object> response = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * 4. Trata erros de violação de integridade do banco de dados (NULL, Foreign Key, Unique). Retorna 400 Bad Request.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        String message = "Erro de integridade de dados. Verifique a unicidade, chaves estrangeiras ou campos obrigatórios.";

        if (ex.getRootCause() != null && ex.getRootCause().getMessage().contains("violates")) {
            // Tenta extrair a mensagem de erro específica do PostgreSQL
            message = ex.getRootCause().getMessage().split("Detalhe: ")[0];
        }

        log.error("ERRO DE DB (400): Violação de Integridade. Mensagem: {}", message);

        Map<String, Object> response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 5. Trata IllegalArgumentException (erros de domínio não específicos ou de parâmetro). Retorna 400 Bad Request.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {

        log.error("REQUISIÇÃO INVÁLIDA (400): {}", ex.getMessage());

        Map<String, Object> response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Requisição inválida: " + ex.getMessage(),
                request
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}