package com.postechfiap.meuhospital.contracts.core;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Record (DTO) para solicitação de cadastro de novo usuário.
 * Contém todos os campos; a validação condicional de registro e nascimento é feita no serviço.
 */
public record UsuarioRegisterRequest(

        @Schema(description = "Nome completo do usuário.", example = "João Medico da Silva")
        @NotBlank(message = "O nome é obrigatório.")
        String nome,

        @Schema(description = "CPF do usuário (11 dígitos).", example = "12345678901")
        @NotBlank(message = "O CPF é obrigatório.")
        @Size(min = 11, max = 11, message = "O CPF deve ter 11 dígitos.")
        String cpf,

        @Schema(description = "E-mail do usuário (usado para login).", example = "medico@hospital.com")
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail deve ser um endereço válido.")
        String email,

        @Schema(description = "Senha do usuário (mínimo 6 caracteres).", example = "123123123")
        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.")
        String senha,

        @Schema(description = "Telefone de contato.", example = "(11) 98765-4321")
        @NotBlank(message = "O telefone é obrigatório.")
        String telefone,

        @Schema(description = "Perfil (Role) do usuário.", example = "MEDICO")
        @NotNull(message = "O perfil (Role) é obrigatório.")
        Role role,

        @Schema(description = "Número de registro profissional (CRM/COREN). Obrigatório para MÉDICO/ENFERMEIRO.", example = "CRM/PE-12345", nullable = true)
        String numeroRegistro,

        @Schema(description = "Especialidade médica. Apenas para MÉDICO.", example = "CARDIOLOGIA", nullable = true)
        String especialidade,

        @Schema(description = "Data de nascimento. Apenas para PACIENTE (formato YYYY-MM-DD).", example = "1980-05-15", nullable = true)
        LocalDate dataNascimento
) {}