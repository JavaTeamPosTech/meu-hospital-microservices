package com.postechfiap.meuhospital.agendamento.entity;

import com.postechfiap.meuhospital.contracts.core.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidade de Projeção (Cópia Local) dos dados do Médico,
 * mantida via eventos Kafka do ms-autenticacao.
 * Serve para consultas de disponibilidade localmente.
 */
@Entity
@Table(name = "medicos_projection")
@Getter
@Setter
public class MedicoProjection {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nome;

    private String numeroRegistro; // CRM/COREN

    private String especialidade;

    @Enumerated(EnumType.STRING)
    private Role role;
}