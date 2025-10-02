package com.postechfiap.meuhospital.agendamento.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando um recurso (Paciente, Médico) não é encontrado em outro MS.
 * Mapeada para 404 NOT FOUND.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String message) {
        super(message);
    }
}