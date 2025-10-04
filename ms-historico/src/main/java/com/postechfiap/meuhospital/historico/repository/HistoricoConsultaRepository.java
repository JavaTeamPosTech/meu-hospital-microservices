package com.postechfiap.meuhospital.historico.repository;

import com.postechfiap.meuhospital.historico.entity.HistoricoConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA para a entidade HistoricoConsulta.
 * Usado para persistir e consultar o histórico (Read-Model).
 */
@Repository
public interface HistoricoConsultaRepository extends JpaRepository<HistoricoConsulta, UUID> {
    List<HistoricoConsulta> findAllByPacienteId(UUID pacienteId);

    List<HistoricoConsulta> findAllByMedicoId(UUID medicoId);

    Optional<HistoricoConsulta> findByPacienteIdAndId(UUID pacienteId, UUID id);
}