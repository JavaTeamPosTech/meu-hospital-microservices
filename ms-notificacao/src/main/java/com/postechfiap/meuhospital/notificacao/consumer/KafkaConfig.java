package com.postechfiap.meuhospital.notificacao.consumer;

import com.postechfiap.meuhospital.contracts.events.ConsultaCriadaEvent;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import java.util.Map;

/**
 * Configuração explícita do Kafka para garantir que o ContainerFactory seja iniciado corretamente
 * em um contexto sem Spring Web.
 */
@EnableKafka
@Configuration
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * Define o ConsumerFactory configurado para desserializar o DTO de evento específico.
     */
    @Bean
    public ConsumerFactory<String, ConsultaCriadaEvent> consumerFactory() {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();

        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ConsultaCriadaEvent.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(ConsultaCriadaEvent.class, false));
    }

    /**
     * Cria o ContainerFactory, que gerencia os @KafkaListener.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsultaCriadaEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ConsultaCriadaEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}