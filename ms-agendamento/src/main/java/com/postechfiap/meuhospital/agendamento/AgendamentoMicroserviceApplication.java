package com.postechfiap.meuhospital.agendamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AgendamentoMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgendamentoMicroserviceApplication.class, args);
	}

}
