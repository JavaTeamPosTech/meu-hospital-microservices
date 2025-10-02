package com.postechfiap.meuhospital.agendamento.controller;

import com.postechfiap.meuhospital.agendamento.service.ConsultaService;
import com.postechfiap.meuhospital.contracts.agendamento.ConsultaRequest;
import com.postechfiap.meuhospital.contracts.agendamento.ConsultaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller responsável pela criação, edição e consulta de agendamentos.
 * Todas as rotas (exceto infraestrutura) requerem autenticação (JWT).
 */
@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    /**
     * Endpoint para criação de uma nova consulta.
     * Requer autoridade: MÉDICO ou ENFERMEIRO.
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ConsultaResponse> criarConsulta(@RequestBody @Valid ConsultaRequest request) {
        ConsultaResponse response = consultaService.criarConsulta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para edição de uma consulta existente.
     * Requer autoridade: MÉDICO ou ENFERMEIRO.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO')")
    public ResponseEntity<ConsultaResponse> editarConsulta(@PathVariable UUID id, @RequestBody @Valid ConsultaRequest request) {
        // NOTE: A lógica de edição no Service deve ser implementada em seguida.
        ConsultaResponse response = consultaService.editarConsulta(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para cancelamento (DELETE lógico) de uma consulta existente.
     * Requer autoridade: MÉDICO ou ENFERMEIRO.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO')")
    public ResponseEntity<Void> cancelarConsulta(@PathVariable UUID id) {
        // NOTE: A lógica de cancelamento no Service deve ser implementada em seguida.
        consultaService.cancelarConsulta(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para buscar detalhes de uma consulta por ID.
     * Regra de autorização (SpEL):
     * - Permite se a Role for MEDICO ou ENFERMEIRO.
     * - OU Permite se o ID do usuário autenticado for o paciente da consulta.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO') or @consultaService.isPacienteDaConsulta(#id, authentication.principal.id)")
    public ResponseEntity<ConsultaResponse> buscarConsultaPorId(@PathVariable UUID id) {
        ConsultaResponse response = consultaService.buscarConsultaPorId(id);
        return ResponseEntity.ok(response);
    }
}