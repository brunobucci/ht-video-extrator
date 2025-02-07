package com.br.fiap.postech.ht_video_extrator.domain.repository;

import org.springframework.messaging.handler.annotation.Payload;

public interface IExtracaoQueueAdapterIN {
	void receive(@Payload String message);
}
