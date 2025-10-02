package com.postechfiap.meuhospital.autenticacao.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que verifica um cabeçalho secreto para liberar chamadas Service-to-Service
 * para endpoints específicos (como GET /usuarios/{id}).
 */
@Component
@Order(1)
public class InternalServiceFilter extends OncePerRequestFilter {

    @Value("${app.internal-secret}")
    private String requiredSecret;

    private static final String INTERNAL_SECRET_HEADER = "X-Internal-Secret";
    private static final String TARGET_PATH = "/usuarios/";

    public InternalServiceFilter() {
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (method.equals("GET") && path.startsWith(TARGET_PATH)) {
            String clientSecret = request.getHeader(INTERNAL_SECRET_HEADER);

            if (requiredSecret.equals(clientSecret)) {
                filterChain.doFilter(request, response);
                return;
            } else {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Acesso negado: Chave de serviço interna inválida.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}