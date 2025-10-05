package com.postechfiap.meuhospital.contracts.core;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Record (DTO) para solicitação de login de usuário.
 */
public record LoginRequest(
        @Schema(description = "E-mail do usuário.", example = "medico1@hospital.com")
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail deve ser um endereço válido.")
        String email,

        @Schema(description = "Senha do usuário.", example = "123123123")
        @NotBlank(message = "A senha é obrigatória.")
        String senha
) {}