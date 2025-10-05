package com.postechfiap.meuhospital.agendamento.controller;

import com.postechfiap.meuhospital.agendamento.dto.MedicoProjectionResponse;
import com.postechfiap.meuhospital.agendamento.service.ConsultaService;
import com.postechfiap.meuhospital.contracts.agendamento.ConsultaRequest;
import com.postechfiap.meuhospital.contracts.agendamento.ConsultaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * Controller responsável pela criação, edição e consulta de agendamentos.
 */
@RestController
@RequestMapping("/consultas")
@Tag(name = "Consultas", description = "Endpoints para agendamento e  gestão de consulta.")
@SecurityRequirement(name = "bearerAuth")
public class ConsultaController {

    private static final Logger log = LoggerFactory.getLogger(ConsultaController.class);

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    /**
     * Endpoint para criação de uma nova consulta.
     * Requer autoridade: MÉDICO ou ENFERMEIRO.
     */
    @PostMapping
    @Operation(summary = "Criar Nova Consulta",
            description = "Cria um novo agendamento, valida a disponibilidade do médico e publica um evento Kafka.")
    @ApiResponse(responseCode = "201", description = "Consulta criada com sucesso.")
    @ApiResponse(responseCode = "400", description = "Regra de Negócio violada (Ex: Conflito de horário, DTO inválido).")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ConsultaResponse> criarConsulta(@RequestBody @Valid ConsultaRequest request) {
        log.info("INICIANDO: POST /consultas. Paciente: {}, Médico: {}, Data: {}",
                request.pacienteId(), request.medicoId(), request.dataConsulta());

        ConsultaResponse response = consultaService.criarConsulta(request);

        log.info("SUCESSO: Consulta ID {} criada e evento publicado.", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para edição de uma consulta existente.
     * Requer autoridade: MÉDICO ou ENFERMEIRO.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Editar Consulta",
            description = "Atualiza a data e/ou detalhes de uma consulta existente.")
    @ApiResponse(responseCode = "200", description = "Consulta atualizada com sucesso.")
    @ApiResponse(responseCode = "404", description = "Consulta não encontrada.")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO')")
    public ResponseEntity<ConsultaResponse> editarConsulta(
            @Parameter(description = "ID da consulta a ser editada.") @PathVariable UUID id,
            @RequestBody @Valid ConsultaRequest request) {

        log.info("INICIANDO: PUT /consultas/{}. Nova Data: {}", id, request.dataConsulta());

        ConsultaResponse response = consultaService.editarConsulta(id, request);

        log.info("SUCESSO: Consulta ID {} editada e evento de ATUALIZAÇÃO publicado.", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para cancelamento (DELETE lógico) de uma consulta existente.
     * Requer autoridade: MÉDICO ou ENFERMEIRO.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar Consulta",
            description = "Marca uma consulta como CANCELADA e publica um evento de cancelamento.")
    @ApiResponse(responseCode = "204", description = "Consulta cancelada com sucesso (No Content).")
    @ApiResponse(responseCode = "404", description = "Consulta não encontrada.")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO')")
    public ResponseEntity<Void> cancelarConsulta(
            @Parameter(description = "ID da consulta a ser cancelada.") @PathVariable UUID id) {

        log.warn("INICIANDO: DELETE /consultas/{}. Processando cancelamento.", id);

        consultaService.cancelarConsulta(id);

        log.warn("SUCESSO: Consulta ID {} cancelada e evento publicado.", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para buscar detalhes de uma consulta por ID.
     * Regra de autorização: Permite a busca pelo proprietário (PACIENTE) ou pela equipe (MÉDICO/ENFERMEIRO).
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar Consulta por ID (Acesso Granular)",
            description = "Retorna uma consulta. Pacientes só podem ver as suas.")
    @ApiResponse(responseCode = "200", description = "Consulta encontrada.")
    @ApiResponse(responseCode = "403", description = "Proibido. Usuário tenta acessar consulta de terceiros.")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO') or @consultaService.isPacienteDaConsulta(#id, authentication.principal.id.toString())")
    public ResponseEntity<ConsultaResponse> buscarConsultaPorId(
            @Parameter(description = "ID da consulta.") @PathVariable UUID id) {

        log.info("Requisição GET /consultas/{} recebida.", id);

        ConsultaResponse response = consultaService.buscarConsultaPorId(id);

        log.info("Busca de consulta ID {} concluída.", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para listar médicos disponíveis, opcionalmente por especialidade.
     * Requer que o usuário esteja autenticado (qualquer Role).
     */
    @GetMapping("/medicos")
    @Operation(summary = "Listar Médicos Disponíveis",
            description = "Retorna a lista de médicos sincronizados (Projeção local).")
    @ApiResponse(responseCode = "200", description = "Lista de médicos retornada com sucesso.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MedicoProjectionResponse>> listarMedicos(
            @Parameter(description = "Filtro opcional por especialidade (ex: CARDIOLOGIA).", required = false) @RequestParam(required = false) String especialidade) {

        log.info("Requisição GET /consultas/medicos recebida. Filtro: {}", especialidade != null ? especialidade : "Nenhum");

        List<MedicoProjectionResponse> medicos = consultaService.listarMedicosDisponiveis(especialidade);

        log.info("Listagem de {} médicos concluída.", medicos.size());
        return ResponseEntity.ok(medicos);
    }
}