package com.postechfiap.meuhospital.autenticacao.service.impl;

import com.postechfiap.meuhospital.autenticacao.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Serviço que implementa a interface UserDetailsService do Spring Security.
 * Responsável por carregar os dados do usuário (UserDetails) pelo username (email) durante o login.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carrega o usuário pelo username (que é o email em nosso sistema).
     * @param username O email do usuário.
     * @return UserDetails (a entidade Usuario, que implementa UserDetails).
     * @throws UsernameNotFoundException se o usuário não for encontrado.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com e-mail: " + username));
    }
}