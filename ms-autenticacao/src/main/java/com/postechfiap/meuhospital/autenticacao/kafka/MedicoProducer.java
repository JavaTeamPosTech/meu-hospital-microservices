package com.postechfiap.meuhospital.autenticacao.kafka;

import com.postechfiap.meuhospital.contracts.events.MedicoEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Componente responsável por enviar eventos de domínio (Médico) para o Kafka.
 */
@Component
public class MedicoProducer {

    private static final String TOPIC_MEDICO_EVENTS = "medico-events";

    private final KafkaTemplate<String, MedicoEvent> kafkaTemplate;

    public MedicoProducer(KafkaTemplate<String, MedicoEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publica um evento sobre o cadastro ou atualização de um médico no Kafka.
     * @param event O DTO de evento (MedicoEvent) com os dados do médico.
     */
    public void sendMedicoEvent(MedicoEvent event) {
        String key = event.userId().toString();

        kafkaTemplate.send(TOPIC_MEDICO_EVENTS, key, event);
        System.out.printf("Kafka: Evento Medico [%s] ID [%s] publicado no tópico %s%n", event.tipoEvento(), key, TOPIC_MEDICO_EVENTS);
    }
}