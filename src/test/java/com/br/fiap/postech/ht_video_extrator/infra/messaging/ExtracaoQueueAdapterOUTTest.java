package com.br.fiap.postech.ht_video_extrator.infra.messaging;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class ExtracaoQueueAdapterOUTTest {

	@InjectMocks
    private ExtracaoQueueAdapterOUT extracaocaoQueueAdapterOUT;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Logger logger;

    @BeforeEach
    public void setUp() {
        // Configurando o valor da fila para o teste
    	extracaocaoQueueAdapterOUT.filaVideosProcessados = "testQueue";
    	extracaocaoQueueAdapterOUT.filaVideosExtraidos = "testQueue";
    	extracaocaoQueueAdapterOUT.filaVideosComNotificacao = "testQueue";
    }

    @Test
    void testPublishVideoProcessado() {
        // Dado um vídeo em formato JSON
        String videoJson = "{\"videoId\":\"12345\", \"status\":\"compactado\"}";

        // Quando o método publishVideoCompactado é chamado
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString());
        
        // Chama o método a ser testado
        extracaocaoQueueAdapterOUT.publishVideoProcessado(videoJson);

        // Então, verifica se o método convertAndSend foi chamado com os parâmetros corretos
        verify(rabbitTemplate).convertAndSend("testQueue", videoJson);
        
        // Verifica se o logger foi chamado (se você estiver usando um logger real, você pode precisar de uma abordagem diferente)
        // Aqui, você pode usar um mock do logger ou verificar a saída do logger se necessário.
        //verify(logger).info("Publicação na fila VideosCompactados executada.");
    }
    
    @Test
    void testPublishVideoComErro() {
        // Dado um vídeo em formato JSON
        String videoJson = "{\"videoId\":\"12345\", \"status\":\"com_erro\"}";

        // Quando o método publishVideoCompactado é chamado
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString());
        
        // Chama o método a ser testado
        extracaocaoQueueAdapterOUT.publishVideoComErro(videoJson);

        // Então, verifica se o método convertAndSend foi chamado com os parâmetros corretos
        verify(rabbitTemplate).convertAndSend("testQueue", videoJson);
        
        // Verifica se o logger foi chamado (se você estiver usando um logger real, você pode precisar de uma abordagem diferente)
        // Aqui, você pode usar um mock do logger ou verificar a saída do logger se necessário.
        //verify(logger).info("Publicação na fila VideosCompactados executada.");
    }
    
    @Test
    void testPublishVideoComNotificacao() {
        // Dado um vídeo em formato JSON
        String videoJson = "{\"videoId\":\"12345\", \"status\":\"com_erro\"}";

        // Quando o método publishVideoCompactado é chamado
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString());
        
        // Chama o método a ser testado
        extracaocaoQueueAdapterOUT.publishVideoComNotificacao(videoJson);

        // Então, verifica se o método convertAndSend foi chamado com os parâmetros corretos
        verify(rabbitTemplate).convertAndSend("testQueue", videoJson);
        
        // Verifica se o logger foi chamado (se você estiver usando um logger real, você pode precisar de uma abordagem diferente)
        // Aqui, você pode usar um mock do logger ou verificar a saída do logger se necessário.
        //verify(logger).info("Publicação na fila VideosCompactados executada.");
    }
}
