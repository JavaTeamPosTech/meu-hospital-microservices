package com.postechfiap.meuhospital.contracts.agendamento;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Record (DTO) para solicitação de criação ou atualização de uma consulta.
 */
public record ConsultaRequest(
        @NotNull(message = "O ID do paciente é obrigatório.")
        @Schema(description = "Identificador único do paciente.", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
        UUID pacienteId,

        @NotBlank(message = "O nome do paciente é obrigatório e deve ser fornecido na requisição.")
        @Schema(description = "Nome completo do paciente.", example = "João da Silva")
        String nomePaciente,

        @NotNull(message = "O ID do médico é obrigatório.")
        @Schema(description = "Identificador único do médico.", example = "a123f1ee-6c54-4b01-90e6-d701748f0851")
        UUID medicoId,

        @NotNull(message = "A data da consulta é obrigatória.")
        @Future(message = "A data da consulta deve ser no futuro.")
        @Schema(description = "Data e hora da consulta, que deve ser no futuro.", example = "2023-12-01T14:30:00")
        LocalDateTime dataConsulta,

        @Schema(description = "Detalhes adicionais sobre a consulta.", example = "Consulta de retorno para avaliação de exames.")
        String detalhesDaConsulta
) {}