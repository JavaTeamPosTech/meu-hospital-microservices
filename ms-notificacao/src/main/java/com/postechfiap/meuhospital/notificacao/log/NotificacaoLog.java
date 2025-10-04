package com.postechfiap.meuhospital.notificacao.log;

import com.postechfiap.meuhospital.contracts.events.ConsultaCriadaEvent;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Documento MongoDB para armazenar o histórico de notificações enviadas.
 */
@Data
@Document(collection = "logs_notificacao")
public class NotificacaoLog {

    @Id
    private String id; // MongoDB usa String para o ID (ObjectId)

    private UUID consultaId;
    private UUID pacienteId;
    private String tipoEvento; // CRIACAO, CANCELAMENTO, ATUALIZACAO
    private String emailDestinatario;
    private String statusEnvio; // SUCESSO, FALHA, PENDENTE
    private LocalDateTime dataEnvio;
    private ConsultaCriadaEvent dadosEvento; // Armazena o payload completo do Kafka para auditoria

    public NotificacaoLog(ConsultaCriadaEvent event, String statusEnvio) {
        this.consultaId = event.consultaId();
        this.pacienteId = event.pacienteId();
        this.tipoEvento = event.tipoEvento();
        this.emailDestinatario = event.emailPaciente();
        this.statusEnvio = statusEnvio;
        this.dataEnvio = LocalDateTime.now();
        this.dadosEvento = event;
    }
}