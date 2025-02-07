package com.br.fiap.postech.ht_video_extrator.application.usecase;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.br.fiap.postech.ht_video_extrator.application.dto.VideoDto;
import com.br.fiap.postech.ht_video_extrator.domain.entity.StatusEdicao;
import com.br.fiap.postech.ht_video_extrator.domain.repository.IExtracaoQueueAdapterOUT;
import com.br.fiap.postech.ht_video_extrator.domain.usecase.IExtrairFramesUseCase;
import com.google.gson.Gson;

import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

@Service
public class ExtrairFrames implements IExtrairFramesUseCase {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    private final IExtracaoQueueAdapterOUT extracaoQueueAdapterOUT;
	
    @Autowired
    private Gson gson;
    
    @Value("${caminhoDaPastaDeVideos}")
    private String caminhoDaPastaDeVideos;
    
    @Value("${caminhoDaPastaDeFrames}")
    private String caminhoDaPastaDeFrames;
    
    @Value("${caminhoDoFfprobe}")
    private String caminhoDoFfprobe;
    
    @Value("${caminhoDoFfmpeg}")
    private String caminhoDoFfmpeg;
    
    public ExtrairFrames(IExtracaoQueueAdapterOUT extracaoQueueAdapterOUT) {
        this.extracaoQueueAdapterOUT = extracaoQueueAdapterOUT;
    }
    
	public void executar(VideoDto videoDto) {

		try {
			logger.info("Iniciou processo de extração de frames.");

			trataDiretorioDosFrames(caminhoDaPastaDeFrames + videoDto.getCodigoEdicao());
			
			executaExtracaoDeFrames(videoDto);
			
			// TODO - Zipar pasta ??
	        
			extracaoQueueAdapterOUT.publishVideoProcessado(toVideoMessage(videoDto));
			
			logger.info("Finalizou processo de extração de frames.");
		}
		catch(Exception ex) {
			//TODO - Publicar na fila de video com erro
			//extracaoQueueAdapterOUT.publishVideoProcessado(toVideoMessage(videoDto));
			logger.error("Video publicado na fila videos_com_erro: ", ex);
		}
	}

	private void executaExtracaoDeFrames(VideoDto videoDto) {

		try {
			String video = videoDto.getNome();
			String videoSemExtensao = video.substring(0, video.indexOf('.'));
			String extensaoArquivo = ".jpg";
			String diretorioDosFramesDoVideo = caminhoDaPastaDeFrames + videoDto.getCodigoEdicao();
			String pathDoVideo = caminhoDaPastaDeVideos+video;
			
			FFprobe ffprobe = new FFprobe(caminhoDoFfprobe);
			FFmpegProbeResult probeResult = ffprobe.probe(pathDoVideo);

			FFmpegFormat format = probeResult.getFormat();
			int segundosTotais = (int) format.duration;
			int incrementoDeSegundos = 20;
					
			for(int segundoAtual = 0; segundoAtual < segundosTotais; segundoAtual += incrementoDeSegundos) {
				String momentoDeRecorte = converteSegundosParaMomentoDoFrame(segundoAtual);
				String nomeFrame = videoSemExtensao+"-"+momentoDeRecorte+extensaoArquivo;
				
				ProcessBuilder processBuilder = new ProcessBuilder(caminhoDoFfmpeg, 
																	"-ss", 
																	momentoDeRecorte, 
																	"-i", 
																	pathDoVideo, 
																	"-frames:v", 
																	"1", 
																	"-q:v", 
																	"2", 
																	diretorioDosFramesDoVideo+"/"+nomeFrame);
				
				Process process = processBuilder.inheritIO().start();
				
				OutputStream outputStream = process.getOutputStream();
				InputStream inputStream = process.getInputStream();
				InputStream errorStream = process.getErrorStream();
				
				printStream(inputStream);
				printStream(errorStream);
				
				boolean isFinished = process.waitFor(30, TimeUnit.SECONDS);
				outputStream.flush();
				outputStream.close();
				
				if(!isFinished) {
					process.destroyForcibly();
				}
			}
		} catch (IOException | InterruptedException e) {
			logger.error("Problema no método trataExtracaoDeFrames.", e);
			e.printStackTrace();
		}
		
	}

	private void trataDiretorioDosFrames(String diretorioDosFramesDoVideo) {
		
		try {
			File diretorioDoVideo = new File(diretorioDosFramesDoVideo);
			if(diretorioDoVideo.exists()) { 
				FileUtils.forceDelete(diretorioDoVideo);
			}
			diretorioDoVideo.mkdir();
		}
		catch(IOException ioEx) {
			logger.error("Problema no método trataDiretorioDosFrames: ", ioEx);
			ioEx.printStackTrace();
		}
	}

	private String converteSegundosParaMomentoDoFrame(int segundos) {
		String retorno = LocalTime.MIN.plusSeconds(segundos).toString();
		return retorno.length() == 5 ? retorno + ":00" : retorno; 
	}

	private static void printStream(InputStream inputStream) throws IOException {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }

        }
    }
	
    private String toVideoMessage(VideoDto video){
        Map message = new HashMap<String, String>();
        message.put("nomeVideo",video.getNome());
        message.put("codigoEdicao",video.getCodigoEdicao().toString());
        message.put("statusEdicao",StatusEdicao.FINALIZADA);
        return gson.toJson(message);
    }
	
}
