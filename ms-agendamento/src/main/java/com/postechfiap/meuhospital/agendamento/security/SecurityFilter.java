package com.postechfiap.meuhospital.agendamento.security;

import com.postechfiap.meuhospital.contracts.core.Role;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

/**
 * Filtro JWT para o ms-agendamento.
 * CRÍTICO: Não faz busca no banco. Apenas valida o token e cria o contexto de segurança
 * com base nas Claims (ID e Role) presentes no JWT.
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityFilter(JwtService jwtService, CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtService = jwtService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = recoverToken(authHeader);

        try {
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 1. Extrai claims essenciais sem ir ao banco
                String login = jwtService.extractUsername(token);
                Role role = Role.valueOf(jwtService.extractClaim(token, claims -> claims.get("role", String.class)));
                UUID userId = jwtService.extractUserId(token);

                // 2. Cria UserDetails "fake" apenas com as claims necessárias para o @PreAuthorize
                UserDetails userDetails = new User(
                        login, // username
                        "", // password (não importa em stateless)
                        Collections.singletonList(new SimpleGrantedAuthority(role.name()))
                ) {
                    // Adiciona o ID do usuário ao objeto principal para uso no SpEL
                    public UUID getId() { return userId; }
                };


                if (jwtService.isTokenValid(token, userDetails)) {
                    // 3. Cria contexto de autenticação
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);

        } catch (JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException("Token inválido, expirado ou formato incorreto.", e)
            );
        }
    }

    private String recoverToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/actuator") ||
                path.equals("/swagger-ui.html"); // Para a página raiz
    }
}