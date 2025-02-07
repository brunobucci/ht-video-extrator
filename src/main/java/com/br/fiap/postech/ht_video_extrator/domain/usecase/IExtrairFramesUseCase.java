package com.br.fiap.postech.ht_video_extrator.domain.usecase;

import com.br.fiap.postech.ht_video_extrator.application.dto.VideoDto;

public interface IExtrairFramesUseCase {
    void executar(VideoDto videoDto);
}
