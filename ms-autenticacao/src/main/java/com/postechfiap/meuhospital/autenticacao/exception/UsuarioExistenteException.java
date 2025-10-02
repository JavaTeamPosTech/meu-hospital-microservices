package com.postechfiap.meuhospital.autenticacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando se tenta criar um usuário (ou outro recurso) que já existe.
 * Mapeia para o status HTTP 409 Conflict.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UsuarioExistenteException extends RuntimeException {

    public UsuarioExistenteException(String message) {
        super(message);
    }
}
