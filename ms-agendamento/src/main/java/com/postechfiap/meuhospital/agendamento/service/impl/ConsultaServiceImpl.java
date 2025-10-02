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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("consultaService")
public class ConsultaServiceImpl implements ConsultaService {

    private static final int DURACAO_PADRAO_MINUTOS = 30;

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
        MedicoProjection medico = validarMedicoExistente(request.medicoId());
        validarDisponibilidade(request.medicoId(), request.dataConsulta());
        PacienteDetails pacienteDetails = buscarValidarPaciente(request);

        Consulta novaConsulta = criarEntidadeConsulta(request, medico, pacienteDetails);
        Consulta consultaSalva = consultaRepository.save(novaConsulta);

        publishConsultaEvent(consultaSalva, pacienteDetails, "CRIACAO");

        return mapToResponse(consultaSalva);
    }

    @Override
    @Transactional
    public ConsultaResponse editarConsulta(UUID id, ConsultaRequest request) {
        Consulta consultaExistente = consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta com ID " + id + " não encontrada para edição."));

        if (consultaExistente.getStatus() != StatusConsulta.AGENDADA) {
            throw new RegraDeNegocioException("Consulta com status " + consultaExistente.getStatus().name() + " não pode ser editada.");
        }

        MedicoProjection medico = validarMedicoExistente(request.medicoId());
        validarDisponibilidade(request.medicoId(), request.dataConsulta());
        PacienteDetails pacienteDetails = buscarValidarPaciente(request);

        consultaExistente.setDataConsulta(request.dataConsulta());
        consultaExistente.setDetalhesDaConsulta(request.detalhesDaConsulta());

        Consulta consultaAtualizada = consultaRepository.save(consultaExistente);

        publishConsultaEvent(consultaAtualizada, pacienteDetails, "ATUALIZACAO");

        return mapToResponse(consultaAtualizada);
    }

    @Override
    @Transactional
    public void cancelarConsulta(UUID id) {
        Consulta consultaExistente = consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta com ID " + id + " não encontrada para cancelamento."));

        if (consultaExistente.getStatus() == StatusConsulta.CANCELADA) {
            throw new RegraDeNegocioException("A consulta já está cancelada.");
        }

        consultaExistente.setStatus(StatusConsulta.CANCELADA);
        consultaExistente.setDetalhesDaConsulta("Cancelado pela equipe em: " + LocalDateTime.now());

        Consulta consultaCancelada = consultaRepository.save(consultaExistente);

        publishConsultaEvent(consultaCancelada, null, "CANCELAMENTO");
    }

    @Override
    public ConsultaResponse buscarConsultaPorId(UUID id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta com ID " + id + " não encontrada."));
        return mapToResponse(consulta);
    }

    @Override
    public boolean isPacienteDaConsulta(UUID consultaId, UUID pacienteId) {
        return consultaRepository.findByIdAndPacienteId(consultaId, pacienteId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicoProjectionResponse> listarMedicosDisponiveis(String especialidade) {
        List<MedicoProjection> medicos;

        if (StringUtils.hasText(especialidade)) {
            medicos = medicoProjectionRepository.findAllByEspecialidade(especialidade);
        } else {
            medicos = medicoProjectionRepository.findAll();
        }

        return medicos.stream()
                .filter(medico -> medico.getRole() == com.postechfiap.meuhospital.contracts.core.Role.MEDICO)
                .map(this::mapToMedicoResponse)
                .collect(Collectors.toList());
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
        PacienteDetails pacienteDetails = authClientService.buscarPacientePorId(request.pacienteId());

        if (!pacienteDetails.nome().equalsIgnoreCase(request.nomePaciente())) {
            throw new RegraDeNegocioException("O nome do paciente ('" + request.nomePaciente() + "') não corresponde ao nome registrado no sistema de autenticação. Confirme a identidade.");
        }
        return pacienteDetails;
    }

    private MedicoProjection validarMedicoExistente(UUID medicoId) {
        return medicoProjectionRepository.findById(medicoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Médico com ID " + medicoId + " não encontrado para agendamento."));
    }

    /**
     * Validação de sobreposição de horário usando duração fixa.
     */
    private void validarDisponibilidade(UUID medicoId, LocalDateTime dataConsulta) {
        LocalDateTime fimConsulta = dataConsulta.plusMinutes(DURACAO_PADRAO_MINUTOS);

        if (consultaRepository.existsByMedicoIdAndDataConsultaBetween(medicoId, dataConsulta, fimConsulta)) {
            throw new RegraDeNegocioException("O médico já possui uma consulta marcada para este horário.");
        }
    }

    private void publishConsultaEvent(Consulta consulta, PacienteDetails pacienteDetails, String tipoEvento) {
        String email = pacienteDetails != null ? pacienteDetails.email() : null;
        String telefone = pacienteDetails != null ? pacienteDetails.telefone() : null;

        ConsultaCriadaEvent event = new ConsultaCriadaEvent(
                consulta.getId(),
                consulta.getPacienteId(),
                consulta.getNomePaciente(),
                email,
                telefone,
                consulta.getMedicoId(),
                consulta.getDataConsulta(),
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