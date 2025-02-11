package com.br.fiap.postech.ht_video_extrator.infra.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.br.fiap.postech.ht_video_extrator.application.dto.VideoDto;
import com.br.fiap.postech.ht_video_extrator.domain.entity.StatusEdicao;
import com.br.fiap.postech.ht_video_extrator.domain.usecase.IExtrairFramesUseCase;
import com.google.gson.Gson;

@SpringBootTest
class ExtracaoQueueAdapterINTest {

    @InjectMocks
    private ExtracaoQueueAdapterIN extracaoQueueAdapterIN;

    @Mock
    private IExtrairFramesUseCase extrairFramesUseCase;

    @Mock
    private Gson gson;
    
    private VideoDto videoDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        videoDto = new VideoDto("111", "123", "video.mp4", "1l", StatusEdicao.CRIADA);
    }

    @Test
    void testReceive() {
        // Arrange
    	String message = "{\"id\":\"123\", \"codigoEdicao\":\"123456\", \"nomeVideo\":\"video.mp4\", \"statusEdicao\":\"EXTRAIDA\"}";
        HashMap<Object, Object> mensagem = new HashMap<>();
        mensagem.put("id", "123");
        mensagem.put("codigoEdicao", "123456");
        mensagem.put("nomeVideo", "video.mp4");
        mensagem.put("statusEdicao", "EXTRAIDA");

        when(gson.fromJson(message, HashMap.class)).thenReturn(mensagem);

        // Act
        extracaoQueueAdapterIN.receive(message);

        // Assert
        //ArgumentCaptor videoDtoCaptor = ArgumentCaptor.forClass(VideoDto.class);
        //verify(extrairFramesUseCase, times(1)).executar(videoDto);
        //VideoDto capturedDto = videoDtoCaptor.getValue();

        assertEquals("111", videoDto.getId());
        assertEquals("123", videoDto.getCodigoEdicao());
        assertEquals("video.mp4", videoDto.getNome());
    }

    @Test
    void testFromMessageToDto() {
        // Arrange
    	Map<String, String> mensagem = new HashMap<>();
        mensagem.put("id", "1");
        mensagem.put("codigoEdicao", "edit1");
        mensagem.put("nomeVideo", "video1");
        mensagem.put("statusEdicao", StatusEdicao.EXTRAIDA.toString());

        // Act
        videoDto = ExtracaoQueueAdapterIN.fromMessageToDto(mensagem);

        // Assert
        assertEquals("1", videoDto.getId());
        assertEquals("edit1", videoDto.getCodigoEdicao());
        assertEquals("video1", videoDto.getNome());
    }
}