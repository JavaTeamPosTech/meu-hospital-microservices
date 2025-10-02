package com.postechfiap.meuhospital.agendamento.service;

import com.postechfiap.meuhospital.agendamento.dto.MedicoProjectionResponse;
import com.postechfiap.meuhospital.contracts.agendamento.ConsultaRequest;
import com.postechfiap.meuhospital.contracts.agendamento.ConsultaResponse;
import java.util.List;

import java.util.UUID;

/**
 * Interface que define o contrato para a gestão de consultas médicas.
 */
public interface ConsultaService {

    ConsultaResponse criarConsulta(ConsultaRequest request);

    ConsultaResponse buscarConsultaPorId(UUID id);

    ConsultaResponse editarConsulta(UUID id, ConsultaRequest request);

    void cancelarConsulta(UUID id);

    /**
     * Retorna a lista de médicos disponíveis, opcionalmente filtrada por especialidade.
     */
    List<MedicoProjectionResponse> listarMedicosDisponiveis(String especialidade);

    /**
     * CRÍTICO: Método usado pelo @PreAuthorize para autorização dinâmica.
     * Verifica se o paciente autenticado é o paciente desta consulta.
     */
    boolean isPacienteDaConsulta(UUID consultaId, UUID pacienteId);
}