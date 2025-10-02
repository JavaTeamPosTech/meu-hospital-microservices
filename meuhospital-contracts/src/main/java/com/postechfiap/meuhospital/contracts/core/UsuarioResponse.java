package com.postechfiap.meuhospital.contracts.core;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Record (DTO) de resposta que representa o usuário, sem incluir dados sensíveis como a senha.
 */
public record UsuarioResponse(
        UUID id,
        String nome,
        String cpf,
        String email,
        String telefone,
        Role role,
        String numeroRegistro,
        String especialidade,
        LocalDate dataNascimento
) {}