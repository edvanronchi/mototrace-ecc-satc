package dev.edvanronchi.workerposition.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.edvanronchi.workerposition.application.dtos.AtualizarSituacaoDto;
import dev.edvanronchi.workerposition.domain.entities.Cordenada;
import dev.edvanronchi.workerposition.domain.enums.Origem;
import dev.edvanronchi.workerposition.domain.enums.Tipo;
import dev.edvanronchi.workerposition.domain.models.Mensagem;
import dev.edvanronchi.workerposition.infra.client.DispositivoClient;
import dev.edvanronchi.workerposition.infra.config.MqttTopics;
import org.springframework.stereotype.Service;

@Service
public class MqttListenerService {

    private final CordenadaService cordenadaService;
    private final DispositivoClient dispositivoClient;

    public MqttListenerService(CordenadaService cordenadaService, DispositivoClient dispositivoClient) {
        this.cordenadaService = cordenadaService;
        this.dispositivoClient = dispositivoClient;
    }

    public void processar(String topico, String payload) {
        if (topico.equals(MqttTopics.CORDENADA)) {
            processarCordenada(payload);
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

    private void processarCordenada(String payload) {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        objectMapper.registerModule(javaTimeModule);

        System.out.println("Listener Cordenada: " + payload);

        try {
            Cordenada cordenada = objectMapper.readValue(payload, Cordenada.class);
            cordenadaService.save(cordenada);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
