package com.postechfiap.meuhospital.autenticacao.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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

    public InternalServiceFilter() {
    }

    /**
     * CRÍTICO: Este método decide se o filtro DEVE ser executado.
     * Devemos executá-lo SOMENTE se for o GET na rota de usuários.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Se NÃO for GET E NÃO começar com /usuarios/, então DEVE SER IGNORADO (true)
        if (!method.equals(TARGET_METHOD) || !path.startsWith(TARGET_PATH)) {
            return true;
        }
        // Se for GET /usuarios/{id}, o filtro deve ser EXECUTADO (false)
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientSecret = request.getHeader(INTERNAL_SECRET_HEADER);

        // Se o cabeçalho secreto não foi enviado, permite que o filtro JWT (SecurityFilter) cuide da autenticação.
        if (clientSecret == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Se a chave secreta foi enviada (chamada Service-to-Service)
        if (requiredSecret.equals(clientSecret)) {
            // SUCESSO: Chave válida. Deixa a requisição passar.
            filterChain.doFilter(request, response);
            return;
        } else {
            // FALHA: Chave inválida. Bloqueia a chamada com 401.
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Acesso negado: Chave de serviço interna inválida.\"}");
            return;
        }
    }
}