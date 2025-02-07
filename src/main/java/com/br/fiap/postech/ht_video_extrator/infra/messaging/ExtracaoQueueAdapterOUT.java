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
	private String filaVideosProcessados;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Override
	public void publishVideoProcessado(String videoJson) {
		rabbitTemplate.convertAndSend(filaVideosProcessados, videoJson);
		logger.info("Publicação na fila VideosProcessados executada.");
	}
}
