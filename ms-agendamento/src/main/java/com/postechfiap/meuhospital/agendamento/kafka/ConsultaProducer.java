package com.postechfiap.meuhospital.agendamento.kafka;

import com.postechfiap.meuhospital.contracts.events.ConsultaCriadaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Produtor Kafka responsável por enviar eventos de consulta (criação/atualização/cancelamento)
 * para o tópico de notificação.
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
     * Publica um evento de consulta no Kafka.
     * * @param event O DTO de evento (ConsultaCriadaEvent) com os dados e tipo de evento.
     */
    public void sendConsultaEvent(ConsultaCriadaEvent event) {
        String key = event.consultaId().toString();

        kafkaTemplate.send(notificacaoTopic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("KAFKA SUCESSO: Evento Consulta [{}] ID [{}] publicado no tópico {}.",
                                event.tipoEvento(), key, notificacaoTopic);
                    } else {
                        log.error("KAFKA FALHA: Erro ao publicar evento Consulta [{}] ID [{}] no tópico {}.",
                                event.tipoEvento(), key, notificacaoTopic, ex);
                    }
                });
    }
}