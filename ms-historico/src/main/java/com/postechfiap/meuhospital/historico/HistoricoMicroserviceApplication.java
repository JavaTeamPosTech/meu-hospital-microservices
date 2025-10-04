package com.postechfiap.meuhospital.historico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.postechfiap.meuhospital.historico.repository")
public class HistoricoMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HistoricoMicroserviceApplication.class, args);
	}

}
