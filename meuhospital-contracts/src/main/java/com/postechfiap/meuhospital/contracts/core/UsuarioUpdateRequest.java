package com.postechfiap.meuhospital.contracts.core;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Record (DTO) para solicitação de atualização de dados de um usuário existente.
 */
public record UsuarioUpdateRequest(
        @NotBlank(message = "O nome é obrigatório.")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail deve ser um endereço válido.")
        String email,

        @NotBlank(message = "O telefone é obrigatório.")
        String telefone,

        @NotNull(message = "O perfil (Role) é obrigatório.")
        Role role,

        // Campos Específicos
        String numeroRegistro,
        String especialidade,
        LocalDate dataNascimento
) {}