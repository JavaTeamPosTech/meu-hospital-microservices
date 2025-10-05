package com.postechfiap.meuhospital.autenticacao.service.impl;

import com.postechfiap.meuhospital.autenticacao.entity.Usuario;
import com.postechfiap.meuhospital.autenticacao.exception.RecursoNaoEncontradoException;
import com.postechfiap.meuhospital.autenticacao.exception.UsuarioExistenteException;
import com.postechfiap.meuhospital.autenticacao.kafka.MedicoProducer;
import com.postechfiap.meuhospital.autenticacao.mapper.UsuarioMapper;
import com.postechfiap.meuhospital.autenticacao.repository.UsuarioRepository;
import com.postechfiap.meuhospital.autenticacao.service.UsuarioService;
import com.postechfiap.meuhospital.contracts.core.Role;
import com.postechfiap.meuhospital.contracts.core.UsuarioRegisterRequest;
import com.postechfiap.meuhospital.contracts.core.UsuarioResponse;
import com.postechfiap.meuhospital.contracts.events.MedicoEvent;
import com.postechfiap.meuhospital.contracts.usuario.PacienteResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementação do Serviço de gestão de usuários (CRUD e validação de domínio).
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final MedicoProducer medicoProducer;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder, MedicoProducer medicoProducer) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.medicoProducer = medicoProducer;
    }

    /**
     * Cria um novo usuário, aplicando criptografia de senha e validações de domínio.
     */
    @Override
    @Transactional
    public UsuarioResponse criarUsuario(UsuarioRegisterRequest request) {
        log.info("Processando criação de novo usuário. E-mail: {}, Role: {}", request.email(), request.role());

        validarUnicidade(request.email(), request.cpf());
        validarCamposCondicionais(request);

        Usuario novoUsuario = usuarioMapper.toEntity(request);
        novoUsuario.setSenha(passwordEncoder.encode(request.senha()));

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        log.info("Usuário ID {} salvo no banco de dados.", usuarioSalvo.getId());

        if (usuarioSalvo.getRole() == Role.MEDICO) {
            publishMedicoEvent(usuarioSalvo, "CRIACAO");
            log.info("Evento MedicoEvent (CRIACAO) publicado para o ID: {}", usuarioSalvo.getId());
        }

        return usuarioMapper.toResponse(usuarioSalvo);
    }

    /**
     * Busca um usuário pelo ID.
     */
    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse buscarUsuarioPorId(UUID id) {
        log.debug("Buscando usuário por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com ID " + id + " não encontrado."));
        return usuarioMapper.toResponse(usuario);
    }

    /**
     * Lista todos os usuários com a Role PACIENTE.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listarPacientes() {
        log.info("Iniciando listagem de todos os pacientes.");
        List<Usuario> pacientes  = usuarioRepository.findByRole(Role.PACIENTE);

        List<PacienteResponse> response = usuarioMapper.toResponseList(pacientes)
                .stream()
                .map(u -> new PacienteResponse(u.id(), u.nome(), u.cpf(), u.email(), u.telefone()))
                .collect(Collectors.toList());

        log.info("Listagem de pacientes concluída. Total: {}", response.size());
        return response;
    }

    /**
     * Busca um usuário pelo e-mail (usado principalmente pelo Spring Security e AuthController).
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        log.debug("Tentativa de buscar usuário para autenticação por e-mail: {}", email);
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Garante que o e-mail e o CPF são únicos antes de salvar.
     */
    private void validarUnicidade(String email, String cpf) {
        log.debug("Validando unicidade para E-mail: {} e CPF: {}", email, cpf);
        if (usuarioRepository.existsByEmail(email)) {
            log.warn("Falha de unicidade: E-mail já existe.");
            throw new UsuarioExistenteException("Email já cadastrado no sistema.");
        }
        if (usuarioRepository.existsByCpf(cpf)) {
            log.warn("Falha de unicidade: CPF já existe.");
            throw new UsuarioExistenteException("CPF já cadastrado no sistema.");
        }
    }

    /**
     * Implementa as regras de validação de domínio baseadas na Role.
     */
    private void validarCamposCondicionais(UsuarioRegisterRequest request) {
        Role role = request.role();
        log.debug("Validando campos condicionais para a Role: {}", role);

        // Validação de Registro Profissional (Médico e Enfermeiro)
        if (role == Role.MEDICO || role == Role.ENFERMEIRO) {
            if (request.numeroRegistro() == null || request.numeroRegistro().isBlank()) {
                log.warn("Validação falhou: {} sem número de registro.", role);
                throw new IllegalArgumentException(role.name() + " deve fornecer um número de registro (CRM/COREN).");
            }
        }

        // Validação de Especialidade (Apenas Médico)
        if (role == Role.MEDICO) {
            if (request.especialidade() == null || request.especialidade().isBlank()) {
                log.warn("Validação falhou: Médico sem especialidade.");
                throw new IllegalArgumentException("Médico deve fornecer a especialidade.");
            }
        } else {
            if (request.especialidade() != null && !request.especialidade().isBlank()) {
                log.warn("Validação falhou: Especialidade definida para Role não-médica.");
                throw new IllegalArgumentException("Apenas médicos podem ter especialidade definida.");
            }
        }

        // Validação de Data de Nascimento (Apenas Paciente)
        if (role == Role.PACIENTE) {
            if (request.dataNascimento() == null) {
                log.warn("Validação falhou: Paciente sem data de nascimento.");
                throw new IllegalArgumentException("Paciente deve fornecer a data de nascimento.");
            }
        } else {
            if (request.dataNascimento() != null) {
                log.warn("Validação falhou: Data de nascimento definida para Role não-paciente.");
                throw new IllegalArgumentException("A data de nascimento é exclusiva para o perfil Paciente.");
            }
        }
    }

    /**
     * Publica o evento no Kafka.
     */
    private void publishMedicoEvent(Usuario medico, String tipoEvento) {
        log.info("Publicando evento MedicoEvent para o tópico. Tipo: {}", tipoEvento);
        MedicoEvent event = new MedicoEvent(
                medico.getId(),
                medico.getNome(),
                medico.getNumeroRegistro(),
                medico.getEspecialidade(),
                medico.getRole(),
                tipoEvento,
                LocalDateTime.now()
        );
        medicoProducer.sendMedicoEvent(event);
    }
}