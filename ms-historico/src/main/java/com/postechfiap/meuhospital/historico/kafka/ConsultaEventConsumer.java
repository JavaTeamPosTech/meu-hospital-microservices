package com.postechfiap.meuhospital.historico.kafka;

import com.postechfiap.meuhospital.contracts.events.ConsultaCriadaEvent;
import com.postechfiap.meuhospital.historico.entity.HistoricoConsulta;
import com.postechfiap.meuhospital.historico.repository.HistoricoConsultaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class ConsultaEventConsumer {

    @Value("${app.kafka.topic-historico}")
    private String historicoTopic;

    private static final Logger log = LoggerFactory.getLogger(ConsultaEventConsumer.class);
    private final HistoricoConsultaRepository repository;

    public ConsultaEventConsumer(HistoricoConsultaRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "${app.kafka.topic-historico}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handle(ConsultaCriadaEvent event) {
        log.info("Evento de Consulta recebido: {} para a consulta ID {}", event.tipoEvento(), event.consultaId());

        HistoricoConsulta historico = repository.findById(event.consultaId()).orElse(new HistoricoConsulta());

        historico.setId(event.consultaId());
        historico.setPacienteId(event.pacienteId());
        historico.setNomePaciente(event.nomePaciente());
        historico.setMedicoId(event.medicoId());
        historico.setNomeMedico(event.nomeMedico());
        historico.setDataConsulta(event.dataConsulta());
        historico.setStatus(event.statusConsulta()); // AGENDADA, CANCELADA, REALIZADA
        historico.setDataRegistro(LocalDateTime.now());

        repository.save(historico);
        log.info("Histórico da consulta ID {} atualizado com status {}.", event.consultaId(), event.statusConsulta());
    }
}