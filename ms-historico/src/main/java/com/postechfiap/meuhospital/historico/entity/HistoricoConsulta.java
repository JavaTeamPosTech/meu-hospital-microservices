package com.postechfiap.meuhospital.historico.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA que representa o estado final de uma consulta para o domínio de leitura (Histórico).
 */
@Entity
@Table(name = "historico_consultas")
@Getter
@Setter
public class HistoricoConsulta {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID pacienteId;

    @Column(nullable = false)
    private UUID medicoId;

    private String nomePaciente;
    private String nomeMedico;

    @Column(nullable = false)
    private LocalDateTime dataConsulta;

    private String status; // Ex: AGENDADA, CANCELADA, REALIZADA

    private LocalDateTime dataRegistro;
}