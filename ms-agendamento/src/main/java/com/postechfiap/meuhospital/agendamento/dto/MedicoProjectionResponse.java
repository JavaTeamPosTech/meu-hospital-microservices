package com.postechfiap.meuhospital.agendamento.dto;

import java.util.UUID;

/**
 * Record (DTO) de resposta para a listagem de médicos disponíveis.
 * Mapeia a MedicoProjection para o cliente.
 */
public record MedicoProjectionResponse(
        UUID id,
        String nome,
        String especialidade,
        String numeroRegistro
) {}