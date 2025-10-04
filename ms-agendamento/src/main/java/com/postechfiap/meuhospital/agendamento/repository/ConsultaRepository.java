package com.postechfiap.meuhospital.agendamento.repository;

import com.postechfiap.meuhospital.agendamento.entity.Consulta;
import com.postechfiap.meuhospital.agendamento.entity.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA para a Entidade Consulta.
 */
@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, UUID> {

    /**
     * Verifica se existe alguma consulta para o médico em um intervalo de tempo,
     * usado para evitar sobreposição de agendamentos.
     */
    boolean existsByMedicoIdAndDataConsultaBetween(UUID medicoId, LocalDateTime start, LocalDateTime end);

    /**
     * Busca uma consulta pelo ID do paciente e ID da consulta (para segurança).
     */
    Optional<Consulta> findByIdAndPacienteId(UUID id, UUID pacienteId);

    /**
     * Busca consultas que ainda estão AGENDADAS, mas cuja data já passou (para marcar como REALIZADA).
     */
    List<Consulta> findAllByStatusAndDataConsultaBefore(StatusConsulta status, LocalDateTime dataLimite);

    /**
     * Busca consultas AGENDADAS entre duas datas (para lembretes do dia seguinte).
     */
    List<Consulta> findAllByStatusAndDataConsultaBetween(StatusConsulta status, LocalDateTime start, LocalDateTime end);
}