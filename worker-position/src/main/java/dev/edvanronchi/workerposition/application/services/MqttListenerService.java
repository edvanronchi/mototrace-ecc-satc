package dev.edvanronchi.workerposition.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.edvanronchi.workerposition.application.dtos.AtualizarSituacaoDto;
import dev.edvanronchi.workerposition.domain.entities.Coordenada;
import dev.edvanronchi.workerposition.domain.enums.Origem;
import dev.edvanronchi.workerposition.domain.enums.Tipo;
import dev.edvanronchi.workerposition.domain.models.Mensagem;
import dev.edvanronchi.workerposition.infra.client.DispositivoClient;
import dev.edvanronchi.workerposition.infra.config.MqttTopics;
import org.springframework.stereotype.Service;

@Service
public class MqttListenerService {

    private final CoordenadaService coordenadaService;
    private final DispositivoClient dispositivoClient;

    public MqttListenerService(CoordenadaService coordenadaService, DispositivoClient dispositivoClient) {
        this.coordenadaService = coordenadaService;
        this.dispositivoClient = dispositivoClient;
    }

    public void processar(String topico, String payload) {
        if (topico.equals(MqttTopics.COORDENADA)) {
            processarCoordenada(payload);
        } else if (topico.equals(MqttTopics.COMUNICACAO)) {
            processarComunicacao(payload);
        }
    }

    private void atualizarSituacaoDispositivo(Mensagem mensagem) {
        AtualizarSituacaoDto body = new AtualizarSituacaoDto(mensagem.getAcao());
        dispositivoClient.atualizarSituacao(mensagem.getCodigoDispositivo(), body);
    }

    private void enviarNotificacao() {
        System.out.println("Enviando notificação...");
    }

    private void processarComunicacao(String payload) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Mensagem mensagem = objectMapper.readValue(payload, Mensagem.class);

            if (Origem.SERVIDOR.equals(mensagem.getOrigem())) {
                return;
            }

            System.out.println("Listener Comunicacao: " + payload);

            if (Tipo.BLOQUEIO.equals(mensagem.getTipo())) {
                atualizarSituacaoDispositivo(mensagem);
            } else if (Tipo.NOTIFICACAO.equals(mensagem.getTipo())) {
                enviarNotificacao();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void processarCoordenada(String payload) {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        objectMapper.registerModule(javaTimeModule);

        System.out.println("Listener Coordenada: " + payload);

        try {
            Coordenada coordenada = objectMapper.readValue(payload, Coordenada.class);
            coordenadaService.save(coordenada);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
