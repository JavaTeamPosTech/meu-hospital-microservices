package com.postechfiap.meuhospital.agendamento.repository;

import com.postechfiap.meuhospital.agendamento.entity.MedicoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA para a Entidade MedicoProjection (cópia local).
 */
@Repository
public interface MedicoProjectionRepository extends JpaRepository<MedicoProjection, UUID> {

    /**
     * Busca um médico ativo pelo seu ID (UUID do ms-autenticacao).
     */
    Optional<MedicoProjection> findById(UUID id);

    /**
     * Lista todos os médicos ativos por especialidade.
     */
    List<MedicoProjection> findAllByEspecialidade(String especialidade);
}