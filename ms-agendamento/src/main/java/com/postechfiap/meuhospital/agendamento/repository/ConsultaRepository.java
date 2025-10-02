package com.postechfiap.meuhospital.agendamento.repository;

import com.postechfiap.meuhospital.agendamento.entity.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
}