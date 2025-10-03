package com.postechfiap.meuhospital.autenticacao.service;

import com.postechfiap.meuhospital.autenticacao.entity.Usuario;
import com.postechfiap.meuhospital.contracts.core.UsuarioRegisterRequest;
import com.postechfiap.meuhospital.contracts.core.UsuarioResponse;
import com.postechfiap.meuhospital.contracts.usuario.PacienteResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface que define o contrato para a gestão de usuários.
 * Separação da interface da implementação para maior flexibilidade e testabilidade.
 */
public interface UsuarioService {

    /**
     * Cria um novo usuário, aplicando criptografia de senha e validações de domínio.
     * @param request DTO de cadastro do usuário.
     * @return DTO de resposta do usuário criado.
     */
    UsuarioResponse criarUsuario(UsuarioRegisterRequest request);

    /**
     * Busca um usuário pelo ID.
     * @param id ID do usuário.
     * @return DTO de resposta do usuário.
     */
    UsuarioResponse buscarUsuarioPorId(UUID id);

    /**
     * Busca um usuário pelo e-mail (usado principalmente pelo AuthController).
     * @param email Email do usuário.
     * @return Entidade Usuario (para uso interno do Service).
     */
    Optional<Usuario> buscarUsuarioPorEmail(String email);

    List<PacienteResponse> listarPacientes();
}
