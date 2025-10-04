package com.postechfiap.meuhospital.notificacao.repository;

import com.postechfiap.meuhospital.notificacao.log.NotificacaoLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório MongoDB para a coleção de logs de notificação.
 */
@Repository
public interface NotificacaoRepository extends MongoRepository<NotificacaoLog, String> {
}