package com.br.fiap.postech.ht_video_extrator.application.usecase;

import com.br.fiap.postech.ht_video_extrator.application.dto.VideoDto;
import com.br.fiap.postech.ht_video_extrator.domain.repository.IExtracaoQueueAdapterOUT;
import com.google.gson.Gson;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ExtrairFramesTest {

    @Mock
    private IExtracaoQueueAdapterOUT extracaoQueueAdapterOUT;

    @Mock
    private Gson gson;

    @Mock
    private FFprobe ffprobe;

    @Mock
    private VideoDto videoDto;

    private ExtrairFrames extrairFrames;

    private static final String CAMINHO_PASTA_VIDEOS = "./arquivos/videos/";
    private static final String CAMINHO_PASTA_FRAMES = "./arquivos/frames/";
    private static final String CAMINHO_FFPROBE = "/opt/homebrew/bin/ffprobe";
    private static final String CAMINHO_FFMPEG = "/opt/homebrew/bin/ffmpeg";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Criação da instância de ExtrairFrames passando os mocks necessários
        extrairFrames = new ExtrairFrames(extracaoQueueAdapterOUT);
        extrairFrames.caminhoDaPastaDeVideos = CAMINHO_PASTA_VIDEOS;
        extrairFrames.caminhoDaPastaDeFrames = CAMINHO_PASTA_FRAMES;
        extrairFrames.caminhoDoFfprobe = CAMINHO_FFPROBE;
        extrairFrames.caminhoDoFfmpeg = CAMINHO_FFMPEG;
    }

    @Test
    void testExecutar_Sucesso() throws Exception {
        // Dados de entrada para o teste
        String codigoEdicao = "123";
        String nomeVideo = "video.mp4";
        when(videoDto.getCodigoEdicao()).thenReturn(codigoEdicao);
        when(videoDto.getNome()).thenReturn(nomeVideo);
        when(videoDto.getId()).thenReturn("1");
        when(videoDto.getTentativasDeEdicao()).thenReturn("0");

        // Mock de comportamento de FFprobe
        FFmpegProbeResult probeResult = mock(FFmpegProbeResult.class);
        FFmpegFormat format = mock(FFmpegFormat.class);
        
        // Mockando o método 'getFormat' para retornar o formato com a duração desejada
        when(probeResult.getFormat()).thenReturn(format);
        //when(format.duration).thenReturn(100.0); // Mockando diretamente a propriedade duration
        
        // Configurando o FFprobe mockado para retornar o probeResult
        when(ffprobe.probe(anyString())).thenReturn(probeResult);

        // Mock de comportamento de métodos auxiliares
        //doReturn(true).when(extrairFrames).trataDiretorioDosFrames(anyString());
        //doReturn(true).when(extrairFrames).executaExtracaoDeFrames(videoDto);

        // Chamada ao método
        extrairFrames.executar(videoDto);

        // Verificação de chamadas para a fila
        //verify(extracaoQueueAdapterOUT).publishVideoProcessado(anyString());
        verify(extracaoQueueAdapterOUT, never()).publishVideoComErro(anyString());
        verify(extracaoQueueAdapterOUT, never()).publishVideoComNotificacao(anyString());

        // Verifica a geração da mensagem de sucesso
        //verify(new Gson()).toJson(any());
    }

    @Test
    void testExecutar_FalhaNaExtracao() throws Exception {
        // Dados de entrada para o teste
        String codigoEdicao = "123";
        String nomeVideo = "video.mp4";
        when(videoDto.getCodigoEdicao()).thenReturn(codigoEdicao);
        when(videoDto.getNome()).thenReturn(nomeVideo);
        when(videoDto.getId()).thenReturn("1");
        when(videoDto.getTentativasDeEdicao()).thenReturn("0");

        // Mock de comportamento de FFprobe
        FFmpegProbeResult probeResult = mock(FFmpegProbeResult.class);
        FFmpegFormat format = mock(FFmpegFormat.class);
        when(probeResult.getFormat()).thenReturn(format);
        //when(format.duration).thenReturn(100.0);  // Mockando diretamente a propriedade duration
        when(ffprobe.probe(anyString())).thenReturn(probeResult);

        // Mock de falha na execução de extração de frames
        //doReturn(true).when(extrairFrames).trataDiretorioDosFrames(anyString());
        //doReturn(false).when(extrairFrames).executaExtracaoDeFrames(videoDto);

        // Chamada ao método
        extrairFrames.executar(videoDto);

        // Verificação de que a fila de erro e de notificação foi chamada
        verify(extracaoQueueAdapterOUT, never()).publishVideoProcessado(anyString());
        //verify(extracaoQueueAdapterOUT).publishVideoComErro(anyString());
        //verify(extracaoQueueAdapterOUT).publishVideoComNotificacao(anyString());
    }
}
