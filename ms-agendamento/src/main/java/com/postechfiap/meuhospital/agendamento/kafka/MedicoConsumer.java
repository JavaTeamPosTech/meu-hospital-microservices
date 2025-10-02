package com.postechfiap.meuhospital.agendamento.kafka;

import com.postechfiap.meuhospital.agendamento.entity.MedicoProjection;
import com.postechfiap.meuhospital.agendamento.repository.MedicoProjectionRepository;
import com.postechfiap.meuhospital.contracts.events.MedicoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor Kafka responsável por receber eventos do ms-autenticacao
 * e manter a MedicoProjection (cópia local dos médicos) atualizada no banco de dados do ms-agendamento.
 */
@Component
public class MedicoConsumer {

    private static final Logger log = LoggerFactory.getLogger(MedicoConsumer.class);
    private final MedicoProjectionRepository repository;

    private static final String TOPIC_MEDICO_EVENTS = "medico-events";

    public MedicoConsumer(MedicoProjectionRepository repository) {
        this.repository = repository;
    }

    /**
     * Listener que consome eventos de atualização/criação de médicos.
     * O 'group-id' é definido no application.yaml como 'agendamento-medicos-group'.
     */
    @KafkaListener(topics = TOPIC_MEDICO_EVENTS, groupId = "${spring.kafka.consumer.group-id}")
    public void consume(MedicoEvent event) {
        log.info("Kafka: Evento MedicoEvent recebido. Tipo: {}", event.tipoEvento());

        MedicoProjection projection = new MedicoProjection();

        projection.setId(event.userId());
        projection.setNome(event.nome());
        projection.setNumeroRegistro(event.numeroRegistro());
        projection.setEspecialidade(event.especialidade());
        projection.setRole(event.role());

        repository.save(projection);

        log.info("MedicoProjection (ID: {}) atualizada/criada com sucesso no DB local.", event.userId());
    }
}