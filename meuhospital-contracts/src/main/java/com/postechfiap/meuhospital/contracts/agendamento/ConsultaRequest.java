package com.postechfiap.meuhospital.contracts.agendamento;

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
        UUID pacienteId,

        @NotBlank(message = "O nome do paciente é obrigatório e deve ser fornecido na requisição.")
        String nomePaciente,

        @NotNull(message = "O ID do médico é obrigatório.")
        UUID medicoId,

        @NotNull(message = "A data da consulta é obrigatória.")
        @Future(message = "A data da consulta deve ser no futuro.")
        LocalDateTime dataConsulta,

        String detalhesDaConsulta
) {}