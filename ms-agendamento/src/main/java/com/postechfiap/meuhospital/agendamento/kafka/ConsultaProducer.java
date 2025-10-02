package com.postechfiap.meuhospital.agendamento.kafka;

import com.postechfiap.meuhospital.contracts.events.ConsultaCriadaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Componente responsável por enviar eventos de consulta (criação/atualização/cancelamento)
 * para o Kafka, a serem consumidos pelo ms-notificacao.
 */
@Component
public class ConsultaProducer {

    private static final Logger log = LoggerFactory.getLogger(ConsultaProducer.class);

    @Value("${app.kafka.topic-notificacao}")
    private String notificacaoTopic;

    private final KafkaTemplate<String, ConsultaCriadaEvent> kafkaTemplate;

    public ConsultaProducer(KafkaTemplate<String, ConsultaCriadaEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publica um evento de criação, atualização ou cancelamento de consulta.
     * @param event O DTO de evento (ConsultaCriadaEvent) com os dados e tipo de evento.
     */
    public void sendConsultaEvent(ConsultaCriadaEvent event) {
        String key = event.consultaId().toString();

        kafkaTemplate.send(notificacaoTopic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Kafka: Evento Consulta [%s] ID [%s] publicado com sucesso no tópico %s",
                                event.tipoEvento(), key, notificacaoTopic);
                    } else {
                        log.error("Kafka: Falha ao publicar evento Consulta [%s] ID [%s] no tópico %s",
                                event.tipoEvento(), key, notificacaoTopic, ex);
                    }
                });
    }
}