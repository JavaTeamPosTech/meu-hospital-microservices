package com.postechfiap.meuhospital.notificacao.service;

import com.postechfiap.meuhospital.contracts.events.ConsultaCriadaEvent;
import com.postechfiap.meuhospital.notificacao.log.NotificacaoLog;
import com.postechfiap.meuhospital.notificacao.repository.NotificacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Serviço responsável por simular o envio de e-mails (via MailHog) e registrar o log de auditoria no MongoDB.
 */
@Service
public class NotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final String TELEFONE_SUPORTE_HOSPITAL = "0800-90900";

    private final JavaMailSender mailSender;
    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoService(JavaMailSender mailSender, NotificacaoRepository notificacaoRepository) {
        this.mailSender = mailSender;
        this.notificacaoRepository = notificacaoRepository;
    }

    /**
     * Envia a notificação com base no evento recebido do Kafka e salva o log.
     */
    public void processarNotificacao(ConsultaCriadaEvent event) {

        log.info("INICIANDO: Processamento de evento de notificação. Tipo: {}, Consulta ID: {}", event.tipoEvento(), event.consultaId());
        String statusEnvio = "SUCESSO";

        try {
            if (event.emailPaciente() == null || event.emailPaciente().isBlank()) {
                log.warn("NOTIFICAÇÃO FALHOU: E-mail do paciente não fornecido no evento ID {}.", event.consultaId());
                statusEnvio = "FALHA - EMAIL AUSENTE";
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("notificacao@meuhospital.com");
            message.setTo(event.emailPaciente());

            String corpo = formatarCorpoEmail(event);

            switch (event.tipoEvento()) {
                case "CRIACAO":
                    message.setSubject("Confirmação de Agendamento");
                    break;
                case "CANCELAMENTO":
                    message.setSubject("Alerta: Cancelamento de Consulta!");
                    break;
                case "LEMBRETE":
                    message.setSubject("LEMBRETE: Sua Consulta é Amanhã!");
                    break;
                default:
                    message.setSubject("Aviso: Alteração na sua Consulta.");
            }

            message.setText(corpo);

            // Simula o envio via MailHog
            mailSender.send(message);
            log.info("ENVIO SUCESSO: E-mail SIMULADO enviado para: {} (Tipo: {})", event.emailPaciente(), event.tipoEvento());

        } catch (Exception e) {
            log.error("FALHA SMTP: Erro ao enviar e-mail via MailHog. Motivo: {}", e.getMessage(), e);
            statusEnvio = "FALHA - ERRO SMTP";
        } finally {
            // Salva o log de auditoria no MongoDB, independentemente do resultado
            NotificacaoLog logEntry = new NotificacaoLog(event, statusEnvio);
            notificacaoRepository.save(logEntry);
            log.info("AUDITORIA SUCESSO: Log de notificação salvo no MongoDB. Status: {}", statusEnvio);
        }
    }

    private String formatarCorpoEmail(ConsultaCriadaEvent event) {
        String data = event.dataConsulta().format(FORMATTER);

        StringBuilder builder = new StringBuilder();
        builder.append("Prezado(a) ").append(event.nomePaciente()).append(",\n\n");

        switch (event.tipoEvento()) {
            case "CRIACAO":
                builder.append("Sua consulta foi confirmada com sucesso.\n");
                break;
            case "CANCELAMENTO":
                builder.append("SUA CONSULTA FOI CANCELADA.\n");
                break;
            case "LEMBRETE":
                builder.append("Este é um lembrete. Sua consulta está marcada para amanhã!\n");
                break;
            case "ATUALIZACAO":
                builder.append("SUA CONSULTA FOI ALTERADA.\n");
                break;
            default:
                builder.append("Detalhes da Consulta:\n");
        }

        builder.append("ID da Consulta: ").append(event.consultaId()).append("\n");
        builder.append("Data e Hora: ").append(data).append("\n");
        builder.append("Médico(a): Dr(a). ").append(event.nomeMedico()).append("\n");
        builder.append("ID do Médico: ").append(event.medicoId()).append("\n\n");

        builder.append("Em caso de dúvidas ou necessidade de reagendamento, entre em contato:\n");
        builder.append("Telefone de Suporte: ").append(TELEFONE_SUPORTE_HOSPITAL).append("\n");

        builder.append("Obrigado,\nEquipe Meu Hospital");

        return builder.toString();
    }
}