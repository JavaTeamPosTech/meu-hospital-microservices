package com.postechfiap.meuhospital.autenticacao.repository;

import com.postechfiap.meuhospital.autenticacao.entity.Usuario;
import com.postechfiap.meuhospital.contracts.core.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA para a Entidade Usuario.
 * Estende JpaRepository para operações CRUD básicas.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    /**
     * Usado pelo Spring Security (UserDetailsService) para carregar o usuário pelo e-mail (username).
     * @param email O e-mail do usuário.
     * @return UserDetails (nossa Entidade Usuario).
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se já existe um usuário cadastrado com o CPF fornecido (para validação de unicidade).
     */
    boolean existsByCpf(String cpf);

    /**
     * Verifica se já existe um usuário cadastrado com o e-mail fornecido (para validação de unicidade).
     */
    boolean existsByEmail(String email);

    /**
     * Busca um usuário pelo e-mail para operações de domínio.
     */
    Optional<Usuario> findOptionalByEmail(String email);

    List<Usuario> findByRole(Role role);
}
