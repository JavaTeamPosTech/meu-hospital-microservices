package com.postechfiap.meuhospital.contracts.core;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Record (DTO) para solicitação de login de usuário.
 */
public record LoginRequest(
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail deve ser um endereço válido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        String senha
) {}
