package com.br.fiap.postech.ht_video_extrator.infra.messaging;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.br.fiap.postech.ht_video_extrator.application.dto.VideoDto;
import com.br.fiap.postech.ht_video_extrator.domain.repository.IExtracaoQueueAdapterIN;
import com.br.fiap.postech.ht_video_extrator.domain.usecase.IExtrairFramesUseCase;
import com.google.gson.Gson;

@Service
public class ExtracaoQueueAdapterIN implements IExtracaoQueueAdapterIN{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private Gson gson;
	
	private IExtrairFramesUseCase extrairFramesUseCase;
	
	public ExtracaoQueueAdapterIN(IExtrairFramesUseCase extrairFramesUseCase) {
		this.extrairFramesUseCase = extrairFramesUseCase;
	}
	
	@SuppressWarnings("unchecked")
	@RabbitListener(queues = {"${queue1.name}"})
	@Override
	public void receive(@Payload String message) {
		HashMap<String, String> mensagem = gson.fromJson(message, HashMap.class);
		VideoDto videoDto = fromMessageToDto(mensagem);
		
		extrairFramesUseCase.executar(videoDto);
		
	}
	
	private static VideoDto fromMessageToDto(Map mensagem) {
		return new VideoDto(
				(String) mensagem.get("codigoEdicao"),
				(String) mensagem.get("nomeVideo"), 
				null);
	}
}
