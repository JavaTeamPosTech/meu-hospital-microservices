package com.postechfiap.meuhospital.autenticacao.service.impl;

import com.postechfiap.meuhospital.autenticacao.entity.Usuario;
import com.postechfiap.meuhospital.autenticacao.exception.RecursoNaoEncontradoException;
import com.postechfiap.meuhospital.autenticacao.exception.UsuarioExistenteException;
import com.postechfiap.meuhospital.autenticacao.mapper.UsuarioMapper;
import com.postechfiap.meuhospital.autenticacao.repository.UsuarioRepository;
import com.postechfiap.meuhospital.autenticacao.service.UsuarioService;
import com.postechfiap.meuhospital.contracts.core.Role;
import com.postechfiap.meuhospital.contracts.core.UsuarioRegisterRequest;
import com.postechfiap.meuhospital.contracts.core.UsuarioResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do Serviço de gestão de usuários (CRUD e validação de domínio).
 * Implementa a validação condicional de campos com base na Role do usuário.
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Cria um novo usuário, aplicando criptografia de senha e validações de domínio.
     */
    @Override
    @Transactional
    public UsuarioResponse criarUsuario(UsuarioRegisterRequest request) {
        validarUnicidade(request.email(), request.cpf());
        validarCamposCondicionais(request);

        Usuario novoUsuario = usuarioMapper.toEntity(request);

        novoUsuario.setSenha(passwordEncoder.encode(request.senha()));

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        return usuarioMapper.toResponse(usuarioSalvo);
    }

    /**
     * Busca um usuário pelo ID.
     */
    @Override
    public UsuarioResponse buscarUsuarioPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com ID " + id + " não encontrado."));
        return usuarioMapper.toResponse(usuario);
    }

    /**
     * Busca um usuário pelo e-mail (usado principalmente pelo Spring Security e AuthController).
     */
    @Override
    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Garante que o e-mail e o CPF são únicos antes de salvar.
     */
    private void validarUnicidade(String email, String cpf) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new UsuarioExistenteException("Email já cadastrado no sistema.");
        }
        if (usuarioRepository.existsByCpf(cpf)) {
            throw new UsuarioExistenteException("CPF já cadastrado no sistema.");
        }
    }

    /**
     * Implementa as regras de validação de domínio baseadas na Role.
     * Exemplo: Médico deve ter número de registro e especialidade.
     */
    private void validarCamposCondicionais(UsuarioRegisterRequest request) {
        Role role = request.role();

        if (role == Role.MEDICO || role == Role.ENFERMEIRO) {
            if (request.numeroRegistro() == null || request.numeroRegistro().isBlank()) {
                throw new IllegalArgumentException(role.name() + " deve fornecer um número de registro (CRM/COREN).");
            }
        }

        if (role == Role.MEDICO) {
            if (request.especialidade() == null || request.especialidade().isBlank()) {
                throw new IllegalArgumentException("Médico deve fornecer a especialidade.");
            }
        } else {
            if (request.especialidade() != null && !request.especialidade().isBlank()) {
                throw new IllegalArgumentException("Apenas médicos podem ter especialidade definida.");
            }
        }

        if (role == Role.PACIENTE) {
            if (request.dataNascimento() == null) {
                throw new IllegalArgumentException("Paciente deve fornecer a data de nascimento.");
            }
            if (request.numeroRegistro() != null && !request.numeroRegistro().isBlank()) {
                throw new IllegalArgumentException("Pacientes não devem ter número de registro profissional.");
            }
        } else {
            if (request.dataNascimento() != null) {
                throw new IllegalArgumentException("A data de nascimento é exclusiva para o perfil Paciente.");
            }
        }
    }
}