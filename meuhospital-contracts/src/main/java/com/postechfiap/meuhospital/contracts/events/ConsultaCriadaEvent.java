package com.postechfiap.meuhospital.contracts.events;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Record (DTO) de Evento de Domínio enviado via Kafka quando uma consulta é criada ou atualizada.
 * Contém dados essenciais para o serviço de Notificação (como contatos do paciente).
 */
public record ConsultaCriadaEvent(
        UUID consultaId,
        UUID pacienteId,
        String nomePaciente,
        String emailPaciente,
        String telefonePaciente,
        UUID medicoId,
        LocalDateTime dataConsulta,
        String tipoEvento, // Ex: "CRIACAO" ou "ATUALIZACAO"
        LocalDateTime eventTimestamp
) {}