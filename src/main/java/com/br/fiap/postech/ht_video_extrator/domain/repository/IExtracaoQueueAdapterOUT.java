package com.br.fiap.postech.ht_video_extrator.domain.repository;

public interface IExtracaoQueueAdapterOUT {
	void publishVideoProcessado(String videoJson);
}
