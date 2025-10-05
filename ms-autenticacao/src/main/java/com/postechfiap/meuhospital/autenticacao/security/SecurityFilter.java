package com.postechfiap.meuhospital.autenticacao.security;

import com.postechfiap.meuhospital.autenticacao.repository.UsuarioRepository;
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

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityFilter(JwtService jwtService, UsuarioRepository usuarioRepository, CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = recoverToken(authHeader);

        try {
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                String login = jwtService.extractUsername(token);
                Role role = Role.valueOf(jwtService.extractClaim(token, claims -> claims.get("role", String.class)));
                UUID userId = jwtService.extractUserId(token);

                UserDetails userDetails = new User(
                        login,
                        "",
                        Collections.singletonList(new SimpleGrantedAuthority(role.name()))
                ) {
                    public UUID getId() { return userId; }
                };


                if (jwtService.isTokenValid(token, userDetails)) {
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


}