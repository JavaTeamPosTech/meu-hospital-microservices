package com.postechfiap.meuhospital.historico.controller;

import com.postechfiap.meuhospital.historico.entity.HistoricoConsulta;
import com.postechfiap.meuhospital.historico.repository.HistoricoConsultaRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller GraphQL para consultas do histórico, aplicando regras de autorização baseadas na Role.
 */
@Controller
public class HistoricoConsultaController {

    private static final Logger log = LoggerFactory.getLogger(HistoricoConsultaController.class);

    private final HistoricoConsultaRepository repository;

    public HistoricoConsultaController(HistoricoConsultaRepository repository) {
        this.repository = repository;
    }

    /**
     * Extrai o ID do usuário autenticado a partir do Principal.
     * O ID (UUID) está armazenado no campo 'password' do UserDetails no SecurityFilter.
     */
    private UUID getUserIdFromPrincipal(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        // Acessa o ID injetado no campo 'password' pelo SecurityFilter
        String userIdString = principal.getPassword();
        return UUID.fromString(userIdString);
    }

    /**
     * Retorna o histórico de consultas. Médicos/Enfermeiros veem todos. Pacientes veem apenas os seus.
     */
    @QueryMapping
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    @Transactional(readOnly = true)
    public List<HistoricoConsulta> consultasHistorico(Authentication authentication) {
        log.info("GraphQL Query: consultasHistorico. User: {}", authentication.getName());

        UUID userId = getUserIdFromPrincipal(authentication);
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        if (role.equals("MEDICO") || role.equals("ENFERMEIRO")) {
            log.debug("User is {}. Returning all {} records.", role, repository.count());
            return repository.findAll();
        }

        if (role.equals("PACIENTE")) {
            log.debug("User is PACIENTE. Filtering by user ID: {}", userId);
            return repository.findAllByPacienteId(userId);
        }

        return List.of();
    }

    /**
     * Busca uma consulta específica por ID. Acesso somente por quem possui a autorização ou é o paciente.
     */
    @QueryMapping
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO') or @historicoConsultaRepository.findByPacienteIdAndId(#id, #authentication.principal.password).isPresent()")
    @Transactional(readOnly = true)
    public HistoricoConsulta consultaHistoricoPorId(
            @Argument UUID id,
            Authentication authentication) {

        log.info("GraphQL Query: consultaHistoricoPorId. Consulta ID: {}", id);
        return repository.findById(id).orElse(null);
    }

    /**
     * Busca consultas por ID do Paciente (acesso restrito à equipe).
     */
    @QueryMapping
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO')")
    @Transactional(readOnly = true)
    public List<HistoricoConsulta> consultasHistoricoPorPaciente(@Argument UUID pacienteId) {
        log.info("GraphQL Query: consultasHistoricoPorPaciente. Paciente ID: {}", pacienteId);
        return repository.findAllByPacienteId(pacienteId);
    }

    /**
     * Busca consultas por ID do Médico (acesso restrito à equipe).
     */
    @QueryMapping
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO')")
    @Transactional(readOnly = true)
    public List<HistoricoConsulta> consultasHistoricoPorMedico(@Argument UUID medicoId) {
        log.info("GraphQL Query: consultasHistoricoPorMedico. Médico ID: {}", medicoId);
        return repository.findAllByMedicoId(medicoId);
    }
}