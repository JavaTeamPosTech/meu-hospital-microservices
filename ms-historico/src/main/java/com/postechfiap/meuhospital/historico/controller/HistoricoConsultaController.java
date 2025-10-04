package com.postechfiap.meuhospital.historico.controller;

import com.postechfiap.meuhospital.historico.entity.HistoricoConsulta;
import com.postechfiap.meuhospital.historico.repository.HistoricoConsultaRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Controller GraphQL que mapeia as consultas (Queries) definidas no schema.graphqls
 * para os métodos do repositório.
 */
@Controller
public class HistoricoConsultaController {

    private final HistoricoConsultaRepository repository;

    public HistoricoConsultaController(HistoricoConsultaRepository repository) {
        this.repository = repository;
    }

    /**
     * A API GraphQL DEVE SER PROTEGIDA
     * Requer JWT válido.
     */
    @QueryMapping
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    @Transactional(readOnly = true)
    public List<HistoricoConsulta> consultasHistorico(Authentication authentication) {
        UUID userId = UUID.fromString(((UserDetails) authentication.getPrincipal()).getPassword());
        String role = authentication.getAuthorities().iterator().next().getAuthority(); // Ex: MEDICO

        if (role.equals("MEDICO") || role.equals("ENFERMEIRO")) {
            return repository.findAll();
        }

        if (role.equals("PACIENTE")) {
            return repository.findAllByPacienteId(userId);
        }

        return List.of();
    }

    /**
     * Busca por ID. Requer autenticação.
     * Pacientes só podem buscar se a consulta for deles.
     */
    @QueryMapping
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO')")
    @Transactional(readOnly = true)
    public HistoricoConsulta consultaHistoricoPorId(@Argument UUID id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * Busca por ID do Paciente (apenas para Médicos/Enfermeiros)
     */
    @QueryMapping
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO')")
    @Transactional(readOnly = true)
    public List<HistoricoConsulta> consultasHistoricoPorPaciente(@Argument UUID pacienteId) {
        return repository.findAll().stream()
                .filter(h -> h.getPacienteId().equals(pacienteId))
                .toList();
    }

    /**
     * Busca por ID do Médico (apenas para Médicos/Enfermeiros)
     */
    @QueryMapping
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO')")
    @Transactional(readOnly = true)
    public List<HistoricoConsulta> consultasHistoricoPorMedico(@Argument UUID medicoId) {
        return repository.findAll().stream()
                .filter(h -> h.getMedicoId().equals(medicoId))
                .toList();
    }
}