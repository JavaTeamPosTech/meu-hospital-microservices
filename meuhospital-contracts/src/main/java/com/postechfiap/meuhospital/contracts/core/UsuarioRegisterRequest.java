package com.postechfiap.meuhospital.contracts.core;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Record (DTO) para solicitação de cadastro de novo usuário.
 * Contém todos os campos de domínio; a validação condicional é feita no Service.
 */
public record UsuarioRegisterRequest(
        @NotBlank(message = "O nome é obrigatório.")
        String nome,

        @NotBlank(message = "O CPF é obrigatório.")
        @Size(min = 11, max = 11, message = "O CPF deve ter 11 dígitos.")
        String cpf,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail deve ser um endereço válido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.")
        String senha,

        @NotBlank(message = "O telefone é obrigatório.")
        String telefone,

        @NotNull(message = "O perfil (Role) é obrigatório.")
        Role role,

        // Campos Específicos (podem ser nulos no DTO, mas validados no Service)
        String numeroRegistro,      // CRM/COREN
        String especialidade,       // Apenas para MEDICO
        LocalDate dataNascimento    // Apenas para PACIENTE
) {}
