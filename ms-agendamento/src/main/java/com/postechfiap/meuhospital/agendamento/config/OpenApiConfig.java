package com.postechfiap.meuhospital.agendamento.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do OpenAPI (Swagger) para o ms-agendamento.
 * Define informações do projeto e o esquema de segurança JWT Bearer.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Meu Hospital - Microserviço de Agendamento")
                        .version("1.0.0")
                        .description("API REST para gestão de consultas (Criação, Edição, Cancelamento) e validação de disponibilidade de médicos.")
                )
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insira o Token JWT (Bearer) obtido do ms-autenticacao.")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}