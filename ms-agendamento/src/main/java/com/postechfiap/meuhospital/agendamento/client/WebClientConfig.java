package com.postechfiap.meuhospital.agendamento.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuração do WebClient (cliente HTTP não bloqueante) para comunicação síncrona
 * entre microserviços (ms-agendamento -> ms-autenticacao).
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}