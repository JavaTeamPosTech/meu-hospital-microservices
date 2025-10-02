package com.postechfiap.meuhospital.agendamento.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA para armazenar as consultas agendadas.
 * Esta é a entidade central do microsserviço de Agendamento.
 */
@Entity
@Table(name = "consultas")
@Getter
@Setter
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID pacienteId;

    @Column(nullable = false)
    private UUID medicoId;

    private String nomePaciente;
    private String nomeMedico;

    @Column(nullable = false)
    private LocalDateTime dataConsulta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConsulta status = StatusConsulta.AGENDADA;

    @Lob
    private String detalhesDaConsulta;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

