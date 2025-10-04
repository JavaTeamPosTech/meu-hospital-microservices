package com.postechfiap.meuhospital.agendamento.scheduler;

import com.postechfiap.meuhospital.agendamento.service.ConsultaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Classe responsável por agendar tarefas de rotina no ms-agendamento,
 * como atualização de status de consultas e envio de lembretes.
 */
@Component
public class AgendamentoScheduler {

    private static final Logger log = LoggerFactory.getLogger(AgendamentoScheduler.class);
    private final ConsultaService consultaService;

    public AgendamentoScheduler(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    /**
     * JOB 1: Altera o status das consultas que passaram da data atual para REALIZADA.
     * Executa todo dia à 01:00 (para processar o dia anterior).
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void atualizarStatusConsultasRealizadas() {
        log.info("INICIANDO JOB: Atualização de status de consultas para REALIZADA.");

        int count = consultaService.marcarConsultasAnterioresComoRealizadas();

        log.info("FINALIZADO JOB: {} consultas marcadas como REALIZADA.", count);
    }

    /**
     * JOB 2: Envia lembretes para consultas AGENDADAS para o dia seguinte.
     * Executa todo dia às 18:00.
     */
    @Scheduled(cron = "0 * * * * *")
    public void enviarLembretesProximoDia() {
        log.info("INICIANDO JOB: Envio de lembretes para consultas do dia seguinte.");
        int count = consultaService.enviarLembretesParaProximoDia();
        log.info("FINALIZADO JOB: {} lembretes publicados no Kafka.", count);
    }
}