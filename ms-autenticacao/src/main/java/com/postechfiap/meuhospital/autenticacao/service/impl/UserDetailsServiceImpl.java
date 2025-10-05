package com.postechfiap.meuhospital.autenticacao.service.impl;

import com.postechfiap.meuhospital.autenticacao.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serviço que implementa a interface UserDetailsService do Spring Security.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carrega o usuário pelo username (que é o email em nosso sistema).
     * @param username O email do usuário.
     * @return UserDetails (a entidade Usuario).
     * @throws UsernameNotFoundException se o usuário não for encontrado.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("INICIANDO: Busca por UserDetails para autenticação: {}", username);

        UserDetails user = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("FALHA DE AUTENTICAÇÃO: Usuário não encontrado com e-mail: {}", username);
                    return new UsernameNotFoundException("Usuário não encontrado com e-mail: " + username);
                });

        log.debug("SUCESSO: UserDetails encontrado para o ID: {}", ((com.postechfiap.meuhospital.autenticacao.entity.Usuario) user).getId());
        return user;
    }
}