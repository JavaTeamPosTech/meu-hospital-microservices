package com.postechfiap.meuhospital.contracts.agendamento;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Record (DTO) de resposta para uma consulta agendada.
 * Retorna todos os detalhes da consulta após a criação ou consulta.
 */
public record ConsultaResponse(
                                UUID id,
                                UUID pacienteId,
                                UUID medicoId,
                                String nomePaciente,
                                String nomeMedico,
                                LocalDateTime dataConsulta,
                                String status,
                                String detalhesDaConsulta,
                                LocalDateTime createdAt
) {}