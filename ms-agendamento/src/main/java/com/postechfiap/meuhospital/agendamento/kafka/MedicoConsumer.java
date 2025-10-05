package com.postechfiap.meuhospital.agendamento.kafka;

import com.postechfiap.meuhospital.agendamento.entity.MedicoProjection;
import com.postechfiap.meuhospital.agendamento.repository.MedicoProjectionRepository;
import com.postechfiap.meuhospital.contracts.events.MedicoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consumidor Kafka responsável por receber eventos do ms-autenticacao
 * e manter a MedicoProjection (cópia local dos médicos) atualizada.
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
     */
    @KafkaListener(topics = TOPIC_MEDICO_EVENTS, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void consume(MedicoEvent event) {
        log.info("KAFKA CONSUMO: Evento MedicoEvent recebido. User ID: {}, Tipo: {}", event.userId(), event.tipoEvento());

        MedicoProjection projection = new MedicoProjection();

        // Mapeamento direto de Evento para Entidade de Projeção
        projection.setId(event.userId());
        projection.setNome(event.nome());
        projection.setNumeroRegistro(event.numeroRegistro());
        projection.setEspecialidade(event.especialidade());
        projection.setRole(event.role());

        repository.save(projection);

        log.info("KAFKA SUCESSO: MedicoProjection ID {} atualizada/criada no DB local.", event.userId());
    }
}