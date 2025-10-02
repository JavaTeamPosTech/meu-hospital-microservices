package com.postechfiap.meuhospital.autenticacao.mapper;

import com.postechfiap.meuhospital.autenticacao.entity.Usuario;
import com.postechfiap.meuhospital.contracts.core.UsuarioRegisterRequest;
import com.postechfiap.meuhospital.contracts.core.UsuarioResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Componente responsável por mapear (converter) a Entidade Usuario para DTOs de Contrato
 * e vice-versa, seguindo o padrão de injeção de dependência (@Component).
 */
@Component
public class UsuarioMapper {

    /**
     * Converte um DTO de Requisição de Cadastro para a Entidade Usuario.
     * @param request DTO de requisição de cadastro.
     * @return Entidade Usuario pronta para ser persistida (com senha em texto plano).
     */
    public Usuario toEntity(UsuarioRegisterRequest request) {
        if (request == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setCpf(request.cpf());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());
        usuario.setTelefone(request.telefone());
        usuario.setRole(request.role());
        usuario.setNumeroRegistro(request.numeroRegistro());
        usuario.setEspecialidade(request.especialidade());
        usuario.setDataNascimento(request.dataNascimento());
        return usuario;
    }

    /**
     * Converte a Entidade Usuario para o DTO de Resposta (UsuarioResponse).
     * @param entity Entidade Usuario persistida.
     * @return DTO UsuarioResponse, sem a senha.
     */
    public UsuarioResponse toResponse(Usuario entity) {
        if (entity == null) {
            return null;
        }

        return new UsuarioResponse(
                entity.getId(),
                entity.getNome(),
                entity.getCpf(),
                entity.getEmail(),
                entity.getTelefone(),
                entity.getRole(),
                entity.getNumeroRegistro(),
                entity.getEspecialidade(),
                entity.getDataNascimento()
        );
    }

    /**
     * Converte uma lista de Entidades Usuario para uma lista de DTOs de Resposta.
     */
    public List<UsuarioResponse> toResponseList(List<Usuario> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}