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

        String statusEnvio = "SUCESSO";

        try {
            if (event.emailPaciente() == null || event.emailPaciente().isBlank()) {
                log.warn("Notificação ignorada: E-mail do paciente {} não fornecido no evento.", event.nomePaciente());
                statusEnvio = "FALHA - EMAIL AUSENTE";
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("notificacao@meuhospital.com");
            message.setTo(event.emailPaciente());

            String corpo = formatarCorpoEmail(event);

            if ("CRIACAO".equals(event.tipoEvento())) {
                message.setSubject("Confirmação de Agendamento");
            } else if ("CANCELAMENTO".equals(event.tipoEvento())) {
                message.setSubject("Alerta: Cancelamento de Consulta!");
            } else {
                message.setSubject("Aviso: Alteração na sua Consulta.");
            }

            message.setText(corpo);

            mailSender.send(message);
            log.info("E-mail SIMULADO enviado via MailHog para: {} (Tipo: {})", event.emailPaciente(), event.tipoEvento());

        } catch (Exception e) {
            log.error("Falha ao enviar e-mail via MailHog para {}: {}", event.emailPaciente(), e.getMessage());
            statusEnvio = "FALHA - ERRO SMTP";
        } finally {
            NotificacaoLog logEntry = new NotificacaoLog(event, statusEnvio);
            notificacaoRepository.save(logEntry);
            log.info("Log de notificação salvo no MongoDB. Status: {}", statusEnvio);
        }
    }

    private String formatarCorpoEmail(ConsultaCriadaEvent event) {
        String data = event.dataConsulta().format(FORMATTER);

        StringBuilder builder = new StringBuilder();
        builder.append("Prezado(a) ").append(event.nomePaciente()).append(",\n\n");

        if ("CRIACAO".equals(event.tipoEvento())) {
            builder.append("Sua consulta foi confirmada com sucesso.\n");
            builder.append("Detalhes:\n");
        } else if ("CANCELAMENTO".equals(event.tipoEvento())) {
            builder.append("SUA CONSULTA FOI CANCELADA.\n");
            builder.append("Detalhes: Por favor, entre em contato com o hospital. \n");
        } else if ("ATUALIZACAO".equals(event.tipoEvento())) {
            builder.append("SUA CONSULTA FOI ALTERADA.\n");
            builder.append("Favor verificar o novo horário e detalhes.\n");
        }

        builder.append("ID da Consulta: ").append(event.consultaId()).append("\n");
        builder.append("Data e Hora: ").append(data).append("\n");
        builder.append("ID do Médico: ").append(event.medicoId()).append("\n\n");
        builder.append("Telefone de Contato: ").append(event.telefonePaciente()).append("\n\n");
        builder.append("Obrigado,\nEquipe Meu Hospital");

        return builder.toString();
    }
}