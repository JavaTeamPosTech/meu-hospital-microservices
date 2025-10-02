package com.postechfiap.meuhospital.contracts.events;

import com.postechfiap.meuhospital.contracts.core.Role;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Record (DTO) de Evento de Domínio enviado via Kafka quando um MEDICO é criado ou atualizado.
 * Este evento é consumido pelo ms-agendamento para manter uma lista local de médicos disponíveis.
 */
public record MedicoEvent(
        UUID userId,
        String nome,
        String numeroRegistro,
        String especialidade,
        Role role,
        String tipoEvento,     // Ex: "CRIACAO" ou "ATUALIZACAO"
        LocalDateTime eventTimestamp
) {}