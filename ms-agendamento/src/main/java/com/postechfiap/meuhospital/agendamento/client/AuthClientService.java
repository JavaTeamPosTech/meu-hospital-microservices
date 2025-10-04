package com.postechfiap.meuhospital.agendamento.client;

import com.postechfiap.meuhospital.agendamento.dto.PacienteDetails;
import com.postechfiap.meuhospital.agendamento.exception.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Cliente REST síncrono para buscar detalhes de usuários no ms-autenticacao.
 * Usa chave secreta (X-Internal-Secret) para autenticação Service-to-Service.
 */
@Service
public class AuthClientService {

    private final WebClient webClient;
    private final String authServiceBaseUrl; // Agora final
    private final String internalSecret;     // Agora final

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
     * @return PacienteDetails (inclui nome, email, telefone).
     * @throws RecursoNaoEncontradoException se o usuário não for encontrado (404).
     */
    public PacienteDetails buscarPacientePorId(UUID userId) {
        String url = authServiceBaseUrl + "/" + userId.toString();

        try {
            return webClient.get()
                    .uri(url)
                    .header(INTERNAL_SECRET_HEADER, internalSecret)
                    .retrieve()

                    .onStatus(status -> status == HttpStatus.NOT_FOUND,
                            clientResponse -> Mono.error(new RecursoNaoEncontradoException("Usuário com ID " + userId + " não encontrado no sistema de Autenticação.")))

                    .bodyToMono(PacienteDetails.class)
                    .block();

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN || e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("Falha na autenticação de serviço: Chave interna inválida ou acesso negado.", e);
            }
            throw new RuntimeException("Falha na comunicação síncrona com o ms-autenticacao: " + e.getMessage(), e);
        }
    }
}