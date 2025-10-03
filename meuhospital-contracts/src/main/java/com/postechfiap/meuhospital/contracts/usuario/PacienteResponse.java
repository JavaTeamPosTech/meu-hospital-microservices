package com.postechfiap.meuhospital.contracts.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

/**
 * Record (DTO) de resposta para a listagem de pacientes disponíveis.
 * Contém os dados essenciais para identificação e contato.
 */
public record PacienteResponse(

        @Schema(description = "ID único do paciente.", example = "5b236fb4-918b-4cea-ab38-b7a97fac52f3")
        UUID id,

        @Schema(description = "Nome completo do paciente.", example = "Pedro Alvares")
        String nome,

        @Schema(description = "CPF do paciente.", example = "12345678901")
        String cpf,

        @Schema(description = "E-mail do paciente.", example = "pedro@exemplo.com")
        String email,

        @Schema(description = "Telefone do paciente.", example = "81999998888")
        String telefone
) {}