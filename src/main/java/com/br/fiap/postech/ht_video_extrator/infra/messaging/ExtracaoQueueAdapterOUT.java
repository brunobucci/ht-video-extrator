package com.br.fiap.postech.ht_video_extrator.infra.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.br.fiap.postech.ht_video_extrator.domain.repository.IExtracaoQueueAdapterOUT;

@Service
public class ExtracaoQueueAdapterOUT implements IExtracaoQueueAdapterOUT{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${queue2.name}")
	private String filaVideosExtraidos;
	
	@Value("${queue3.name}")
	private String filaVideosProcessados;
	
	@Value("${queue4.name}")
	private String filaVideosComNotificacao;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Override
	public void publishVideoProcessado(String videoJson) {
		rabbitTemplate.convertAndSend(filaVideosExtraidos, videoJson);
		logger.info("Publicação na fila videos_extraidos executada.");
	}

	@Override
	public void publishVideoComErro(String videoJson) {
		rabbitTemplate.convertAndSend(filaVideosProcessados, videoJson);
		logger.info("Publicação na fila videos_processados com erro executada.");
	}
	
	@Override
	public void publishVideoComNotificacao(String videoJson) {
		rabbitTemplate.convertAndSend(filaVideosComNotificacao, videoJson);
		logger.info("Publicação na fila videos_com_notificacao com erro executada.");
	}
}
