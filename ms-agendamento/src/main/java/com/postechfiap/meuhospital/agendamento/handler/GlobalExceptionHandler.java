package com.postechfiap.meuhospital.agendamento.handler;

import com.postechfiap.meuhospital.agendamento.exception.RecursoNaoEncontradoException;
import com.postechfiap.meuhospital.agendamento.exception.RegraDeNegocioException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Tratador Global de Exceções (@ControllerAdvice) para o ms-agendamento.
 * Centraliza a resposta para erros de validação, integridade de dados e regras de negócio violadas.
 */
@ControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    /**
     * Constrói uma resposta de erro padronizada.
     */
    private Map<String, Object> buildErrorResponse(HttpStatus status, String message, String path) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", path);
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

        Map<String, Object> response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Erros de validação encontrados na requisição.",
                request.getRequestURI()
        );
        response.put("validationErrors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 2. Trata exceções de Regra de Negócio (ex: Médico indisponível, Horário inválido). Retorna 400 Bad Request.
     */
    @ExceptionHandler(RegraDeNegocioException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleRegraDeNegocioException(
            RegraDeNegocioException ex, HttpServletRequest request) {

        Map<String, Object> response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 3. Trata exceções de recurso não encontrado (lançadas pelo serviço ou pelo AuthClientService). Retorna 404 Not Found.
     */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex, HttpServletRequest request) {

        Map<String, Object> response = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * 4. Trata erros de violação de integridade do banco de dados (chaves duplicadas, NOT NULL). Retorna 400 Bad Request.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        String message = "Erro de integridade de dados. Verifique unicidade ou campos obrigatórios.";

        if (ex.getRootCause() != null && ex.getRootCause().getMessage().contains("violates")) {
            message = ex.getRootCause().getMessage().split("Detalhe: ")[0];
        }

        Map<String, Object> response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}