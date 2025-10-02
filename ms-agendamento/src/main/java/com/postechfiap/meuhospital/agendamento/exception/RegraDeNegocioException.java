package com.postechfiap.meuhospital.agendamento.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando uma regra de negócio é violada (ex: conflito de horário, médico indisponível).
 * Mapeada para 400 Bad Request pelo GlobalExceptionHandler.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String message) {
        super(message);
    }
}