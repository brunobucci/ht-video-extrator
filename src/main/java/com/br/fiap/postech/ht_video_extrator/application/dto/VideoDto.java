package com.br.fiap.postech.ht_video_extrator.application.dto;

import com.br.fiap.postech.ht_video_extrator.domain.entity.StatusEdicao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoDto {
    private String codigoEdicao;
	private String nome;
    private StatusEdicao statusEdicao;
}
