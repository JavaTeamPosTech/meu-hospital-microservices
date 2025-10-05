package com.postechfiap.meuhospital.autenticacao.security;

import com.postechfiap.meuhospital.contracts.core.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
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
import java.util.List;
import java.util.UUID;

/**
 * Filtro que verifica um cabeçalho secreto para liberar chamadas Service-to-Service
 * para endpoints específicos (como GET /usuarios/{id}).
 */
@Component
public class InternalServiceFilter extends OncePerRequestFilter {

    @Value("${app.internal-secret}")
    private String requiredSecret;

    private static final String INTERNAL_SECRET_HEADER = "X-Internal-Secret";
    private static final String TARGET_PATH = "/usuarios/";
    private static final String TARGET_METHOD = "GET";

    public InternalServiceFilter(@Value("${app.internal-secret}") String requiredSecret) {
        this.requiredSecret = requiredSecret;
    }

    /**
     * Este método decide se o filtro DEVE ser executado.
     * Devemos executá-lo SOMENTE se for o GET na rota de usuários.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        return !method.equals(TARGET_METHOD) || !path.startsWith(TARGET_PATH);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientSecret = request.getHeader(INTERNAL_SECRET_HEADER);

        if (clientSecret == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (requiredSecret.equals(clientSecret)) {
            UserDetails userDetails = new User(
                    "ms-agendamento",
                    "",
                    Collections.singletonList(new SimpleGrantedAuthority("INTERNAL_SERVICE_ACCESS"))
            );

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Acesso negado: Chave de serviço interna inválida.\"}");
            return;
        }
    }
}