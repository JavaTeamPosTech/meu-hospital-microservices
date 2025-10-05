package com.postechfiap.meuhospital.agendamento.client;

import com.postechfiap.meuhospital.agendamento.dto.PacienteDetails;
import com.postechfiap.meuhospital.agendamento.exception.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Cliente REST síncrono para buscar detalhes de usuários no ms-autenticacao.
 * Utiliza chave secreta (X-Internal-Secret) para autenticação Service-to-Service.
 */
@Service
public class AuthClientService {

    private static final Logger log = LoggerFactory.getLogger(AuthClientService.class);

    private final WebClient webClient;
    private final String authServiceBaseUrl;
    private final String internalSecret;

    private static final String INTERNAL_SECRET_HEADER = "X-Internal-Secret";

    public AuthClientService(WebClient webClient,
                             @Value("${app.auth-service-url}") String authServiceBaseUrl,
                             @Value("${app.internal-secret}") String internalSecret) {
        this.webClient = webClient;
        this.authServiceBaseUrl = authServiceBaseUrl;
        this.internalSecret = internalSecret;
    }

    /**
     * Busca os detalhes completos do paciente (nome, email, telefone) no ms-autenticacao.
     * @param userId O ID do paciente.
     * @return PacienteDetails.
     * @throws RecursoNaoEncontradoException se o usuário não for encontrado (404).
     */
    public PacienteDetails buscarPacientePorId(UUID userId) {
        String url = authServiceBaseUrl + "/" + userId.toString();
        log.info("RPC INICIADO: Buscando Paciente ID {} via GET {}", userId, url);

        try {
            PacienteDetails response = webClient.get()
                    .uri(url)
                    .header(INTERNAL_SECRET_HEADER, internalSecret)
                    .retrieve()

                    // Tratamento de 404 (Recurso Não Encontrado)
                    .onStatus(status -> status == HttpStatus.NOT_FOUND,
                            clientResponse -> {
                                log.warn("RPC FALHOU: 404 Not Found para o ID {}", userId);
                                return Mono.error(new RecursoNaoEncontradoException("Usuário com ID " + userId + " não encontrado no sistema de Autenticação."));
                            })

                    .bodyToMono(PacienteDetails.class)
                    .block(); // Chamada síncrona

            log.info("RPC SUCESSO: Detalhes do Paciente ID {} recebidos.", userId);
            return response;

        } catch (WebClientResponseException e) {
            // Tratamento de falhas de segurança (401/403) ou 5xx
            if (e.getStatusCode() == HttpStatus.FORBIDDEN || e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                log.error("RPC FALHOU: Acesso negado ao ms-autenticacao. Status: {}", e.getStatusCode());
                throw new RuntimeException("Falha na autenticação de serviço: Chave interna inválida ou acesso negado.", e);
            }
            log.error("RPC FALHOU: Erro inesperado na comunicação síncrona. Status: {}", e.getStatusCode(), e);
            throw new RuntimeException("Falha na comunicação síncrona com o ms-autenticacao: " + e.getMessage(), e);
        }
    }
}