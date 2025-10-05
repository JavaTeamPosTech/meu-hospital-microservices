package com.postechfiap.meuhospital.agendamento.service.impl;

import com.postechfiap.meuhospital.agendamento.client.AuthClientService;
import com.postechfiap.meuhospital.agendamento.dto.MedicoProjectionResponse;
import com.postechfiap.meuhospital.agendamento.dto.PacienteDetails;
import com.postechfiap.meuhospital.agendamento.entity.Consulta;
import com.postechfiap.meuhospital.agendamento.entity.MedicoProjection;
import com.postechfiap.meuhospital.agendamento.entity.StatusConsulta;
import com.postechfiap.meuhospital.agendamento.exception.RecursoNaoEncontradoException;
import com.postechfiap.meuhospital.agendamento.exception.RegraDeNegocioException;
import com.postechfiap.meuhospital.agendamento.kafka.ConsultaProducer;
import com.postechfiap.meuhospital.agendamento.repository.ConsultaRepository;
import com.postechfiap.meuhospital.agendamento.repository.MedicoProjectionRepository;
import com.postechfiap.meuhospital.agendamento.service.ConsultaService;
import com.postechfiap.meuhospital.contracts.agendamento.ConsultaRequest;
import com.postechfiap.meuhospital.contracts.agendamento.ConsultaResponse;
import com.postechfiap.meuhospital.contracts.events.ConsultaCriadaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("consultaService")
public class ConsultaServiceImpl implements ConsultaService {

    private static final int DURACAO_PADRAO_MINUTOS = 30;

    private static final Logger log = LoggerFactory.getLogger(ConsultaServiceImpl.class);

    private final ConsultaRepository consultaRepository;
    private final MedicoProjectionRepository medicoProjectionRepository;
    private final AuthClientService authClientService;
    private final ConsultaProducer consultaProducer;

    public ConsultaServiceImpl(ConsultaRepository consultaRepository, MedicoProjectionRepository medicoProjectionRepository, AuthClientService authClientService, ConsultaProducer consultaProducer) {
        this.consultaRepository = consultaRepository;
        this.medicoProjectionRepository = medicoProjectionRepository;
        this.authClientService = authClientService;
        this.consultaProducer = consultaProducer;
    }

    @Override
    @Transactional
    public ConsultaResponse criarConsulta(ConsultaRequest request) {
        log.info("INICIANDO CRIAÇÃO DE CONSULTA. Paciente ID: {}, Médico ID: {}, Data: {}",
                request.pacienteId(), request.medicoId(), request.dataConsulta());

        MedicoProjection medico = validarMedicoExistente(request.medicoId());
        validarDisponibilidade(request.medicoId(), request.dataConsulta());
        PacienteDetails pacienteDetails = buscarValidarPaciente(request);

        Consulta novaConsulta = criarEntidadeConsulta(request, medico, pacienteDetails);
        Consulta consultaSalva = consultaRepository.save(novaConsulta);

        publishConsultaEvent(consultaSalva, pacienteDetails, "CRIACAO");
        log.info("SUCESSO: Consulta ID {} criada e evento CRIACAO publicado.", consultaSalva.getId());

        return mapToResponse(consultaSalva);
    }

    @Override
    @Transactional
    public ConsultaResponse editarConsulta(UUID id, ConsultaRequest request) {
        log.info("INICIANDO EDIÇÃO DE CONSULTA ID {}. Nova Data: {}", id, request.dataConsulta());

        Consulta consultaExistente = consultaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Falha de edição: Consulta ID {} não encontrada.", id);
                    return new RecursoNaoEncontradoException("Consulta com ID " + id + " não encontrada para edição.");
                });

        if (consultaExistente.getStatus() != StatusConsulta.AGENDADA) {
            log.warn("Falha de edição: Consulta ID {} não está AGENDADA.", id);
            throw new RegraDeNegocioException("Consulta com status " + consultaExistente.getStatus().name() + " não pode ser editada.");
        }

        MedicoProjection medico = validarMedicoExistente(request.medicoId());
        validarDisponibilidade(request.medicoId(), request.dataConsulta());
        PacienteDetails pacienteDetails = buscarValidarPaciente(request);

        consultaExistente.setDataConsulta(request.dataConsulta());
        consultaExistente.setDetalhesDaConsulta(request.detalhesDaConsulta());

        Consulta consultaAtualizada = consultaRepository.save(consultaExistente);

        publishConsultaEvent(consultaAtualizada, pacienteDetails, "ATUALIZACAO");
        log.info("SUCESSO: Consulta ID {} atualizada e evento ATUALIZACAO publicado.", id);

        return mapToResponse(consultaAtualizada);
    }

    @Override
    @Transactional
    public void cancelarConsulta(UUID id) {
        log.warn("INICIANDO CANCELAMENTO DE CONSULTA ID {}.", id);

        Consulta consultaExistente = consultaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Falha de cancelamento: Consulta ID {} não encontrada.", id);
                    return new RecursoNaoEncontradoException("Consulta com ID " + id + " não encontrada para cancelamento.");
                });

        if (consultaExistente.getStatus() == StatusConsulta.CANCELADA) {
            log.warn("Falha de cancelamento: Consulta ID {} já está CANCELADA.", id);
            throw new RegraDeNegocioException("A consulta já está cancelada.");
        }

        PacienteDetails pacienteDetails = authClientService.buscarPacientePorId(consultaExistente.getPacienteId());

        consultaExistente.setStatus(StatusConsulta.CANCELADA);
        consultaExistente.setDetalhesDaConsulta("Cancelado pela equipe em: " + LocalDateTime.now());

        Consulta consultaCancelada = consultaRepository.save(consultaExistente);

        publishConsultaEvent(consultaCancelada, pacienteDetails, "CANCELAMENTO");
        log.warn("SUCESSO: Consulta ID {} cancelada e evento CANCELAMENTO publicado.", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ConsultaResponse buscarConsultaPorId(UUID id) {
        log.debug("Buscando consulta por ID: {}", id);
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta com ID " + id + " não encontrada."));
        return mapToResponse(consulta);
    }

    @Override
    public boolean isPacienteDaConsulta(UUID consultaId, UUID pacienteId) {
        log.debug("Verificando se Paciente ID {} é proprietário da Consulta ID {}.", pacienteId, consultaId);
        return consultaRepository.findByIdAndPacienteId(consultaId, pacienteId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicoProjectionResponse> listarMedicosDisponiveis(String especialidade) {
        log.info("INICIANDO listagem de médicos. Filtro: {}", StringUtils.hasText(especialidade) ? especialidade : "Nenhum");

        List<MedicoProjection> medicos;

        if (StringUtils.hasText(especialidade)) {
            medicos = medicoProjectionRepository.findAllByEspecialidade(especialidade);
        } else {
            medicos = medicoProjectionRepository.findAll();
        }

        List<MedicoProjectionResponse> response = medicos.stream()
                .filter(medico -> medico.getRole() == com.postechfiap.meuhospital.contracts.core.Role.MEDICO)
                .map(this::mapToMedicoResponse)
                .collect(Collectors.toList());

        log.info("Listagem de médicos concluída. Total: {}", response.size());
        return response;
    }

    @Override
    @Transactional
    public int marcarConsultasAnterioresComoRealizadas() {
        LocalDateTime ontemAmeiaNoite = LocalDate.now().atStartOfDay();
        log.info("JOB SCHEDULER: Iniciando marcação de consultas REALIZADAS antes de {}.", ontemAmeiaNoite);

        List<Consulta> consultasExpiradas = consultaRepository.findAllByStatusAndDataConsultaBefore(
                StatusConsulta.AGENDADA,
                ontemAmeiaNoite
        );

        consultasExpiradas.forEach(consulta -> {
            consulta.setStatus(StatusConsulta.REALIZADA);
        });

        consultaRepository.saveAll(consultasExpiradas);
        log.info("JOB CONCLUÍDO: {} consultas marcadas como REALIZADA.", consultasExpiradas.size());
        return consultasExpiradas.size();
    }

    @Override
    @Transactional
    public int enviarLembretesParaProximoDia() {
        LocalDate amanha = LocalDate.now().plusDays(1);
        LocalDateTime inicioAmanha = amanha.atStartOfDay();
        LocalDateTime fimAmanha = amanha.atTime(LocalTime.MAX);
        log.info("JOB SCHEDULER: Buscando consultas AGENDADAS para o dia {}.", amanha);

        List<Consulta> consultasAmanha = consultaRepository.findAllByStatusAndDataConsultaBetween(
                StatusConsulta.AGENDADA,
                inicioAmanha,
                fimAmanha
        );

        consultasAmanha.forEach(consulta -> {
            try {
                PacienteDetails pacienteDetails = authClientService.buscarPacientePorId(consulta.getPacienteId());
                publishConsultaEvent(consulta, pacienteDetails, "LEMBRETE");
            } catch (Exception e) {
                log.error("ERRO JOB: Falha ao buscar detalhes do paciente {} para lembrete: {}", consulta.getPacienteId(), e.getMessage());
            }
        });

        log.info("JOB CONCLUÍDO: {} lembretes publicados para amanhã.", consultasAmanha.size());
        return consultasAmanha.size();
    }

    private MedicoProjectionResponse mapToMedicoResponse(MedicoProjection medico) {
        return new MedicoProjectionResponse(
                medico.getId(),
                medico.getNome(),
                medico.getEspecialidade(),
                medico.getNumeroRegistro()
        );
    }

    private PacienteDetails buscarValidarPaciente(ConsultaRequest request) {
        log.debug("RPC SÍNCRONO: Buscando detalhes do paciente {} no ms-autenticacao.", request.pacienteId());

        PacienteDetails pacienteDetails = authClientService.buscarPacientePorId(request.pacienteId());

        if (pacienteDetails == null || pacienteDetails.nome().isBlank()) {
            log.warn("FALHA VALIDAÇÃO: Paciente não encontrado. Recebido: {}",
                    pacienteDetails != null ? pacienteDetails.nome() : "NULO");
            throw new RegraDeNegocioException("Paciente não encontrado.");
        }
        log.debug("RPC SUCESSO: Detalhes do paciente validados.");
        return pacienteDetails;
    }

    private MedicoProjection validarMedicoExistente(UUID medicoId) {
        log.debug("Validando existência do Médico ID: {}", medicoId);
        return medicoProjectionRepository.findById(medicoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Médico com ID " + medicoId + " não encontrado para agendamento."));
    }

    private void validarDisponibilidade(UUID medicoId, LocalDateTime dataConsulta) {
        LocalDateTime fimConsulta = dataConsulta.plusMinutes(DURACAO_PADRAO_MINUTOS);
        log.debug("Validando slot para Médico ID {} entre {} e {}.", medicoId, dataConsulta, fimConsulta);

        if (consultaRepository.existsByMedicoIdAndDataConsultaBetween(medicoId, dataConsulta, fimConsulta)) {
            log.warn("FALHA DISPONIBILIDADE: Conflito de horário encontrado para o Médico ID {}.", medicoId);
            throw new RegraDeNegocioException("O médico já possui uma consulta marcada para este horário.");
        }
    }

    private void publishConsultaEvent(Consulta consulta, PacienteDetails pacienteDetails, String tipoEvento) {
        String email = pacienteDetails != null ? pacienteDetails.email() : null;
        String telefone = pacienteDetails != null ? pacienteDetails.telefone() : null;

        log.debug("Publicando evento {} para Consulta ID {}.", tipoEvento, consulta.getId());

        ConsultaCriadaEvent event = new ConsultaCriadaEvent(
                consulta.getId(),
                consulta.getPacienteId(),
                consulta.getNomePaciente(),
                email,
                telefone,
                consulta.getMedicoId(),
                consulta.getNomeMedico(),
                consulta.getDataConsulta(),
                consulta.getStatus().toString(),
                tipoEvento,
                LocalDateTime.now()
        );
        consultaProducer.sendConsultaEvent(event);
    }

    private Consulta criarEntidadeConsulta(ConsultaRequest request, MedicoProjection medico, PacienteDetails pacienteDetails) {
        Consulta consulta = new Consulta();
        consulta.setPacienteId(request.pacienteId());
        consulta.setNomePaciente(pacienteDetails.nome());
        consulta.setMedicoId(request.medicoId());
        consulta.setNomeMedico(medico.getNome());
        consulta.setDataConsulta(request.dataConsulta());
        consulta.setDetalhesDaConsulta(request.detalhesDaConsulta());
        return consulta;
    }

    private ConsultaResponse mapToResponse(Consulta consulta) {
        return new ConsultaResponse(
                consulta.getId(),
                consulta.getPacienteId(),
                consulta.getMedicoId(),
                consulta.getNomePaciente(),
                consulta.getNomeMedico(),
                consulta.getDataConsulta(),
                consulta.getStatus().name(),
                consulta.getDetalhesDaConsulta(),
                consulta.getCreatedAt()
        );
    }
}