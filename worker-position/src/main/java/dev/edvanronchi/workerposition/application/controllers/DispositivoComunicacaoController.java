package dev.edvanronchi.workerposition.application.controllers;


import dev.edvanronchi.workerposition.application.dtos.CodigoDispositivoDto;
import dev.edvanronchi.workerposition.application.services.DispositivoComunicacaoService;
import dev.edvanronchi.workerposition.application.services.MqttProducerService;
import dev.edvanronchi.workerposition.domain.enums.Acao;
import dev.edvanronchi.workerposition.infra.config.MqttTopics;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/dispositivo-comunicacao")
public class DispositivoComunicacaoController {

    private final MqttProducerService mqttProducerService;
    private final DispositivoComunicacaoService dispositivoComunicacaoService;

    public DispositivoComunicacaoController(MqttProducerService mqttProducerService, DispositivoComunicacaoService dispositivoComunicacaoService) {
        this.mqttProducerService = mqttProducerService;
        this.dispositivoComunicacaoService = dispositivoComunicacaoService;
    }

    @PostMapping("/bloquear")
    public ResponseEntity<?> bloquear(@RequestBody CodigoDispositivoDto dto) {
        String payload = dispositivoComunicacaoService.montarPayload(dto, Acao.BLOQUEAR);
        mqttProducerService.enviarMensagem(MqttTopics.COMUNICACAO, payload);
        return dispositivoComunicacaoService.verificarSeFoiAlterado(dto.codigoDispositivo(), Acao.BLOQUEAR);
    }

    @PostMapping("/desbloquear")
    public ResponseEntity<?> desbloquear(@RequestBody CodigoDispositivoDto dto) {
        String payload = dispositivoComunicacaoService.montarPayload(dto, Acao.DESBLOQUEAR);
        mqttProducerService.enviarMensagem(MqttTopics.COMUNICACAO, payload);
        return dispositivoComunicacaoService.verificarSeFoiAlterado(dto.codigoDispositivo(), Acao.DESBLOQUEAR);
    }

    @PostMapping("/ativar-notificacao")
    public ResponseEntity<?> ativarNotificacao(@RequestBody CodigoDispositivoDto dto) {
        String payload = dispositivoComunicacaoService.montarPayload(dto, Acao.ATIVAR_NOTIFICACAO);
        mqttProducerService.enviarMensagem(MqttTopics.COMUNICACAO, payload);
        return dispositivoComunicacaoService.verificarSeFoiAlterado(dto.codigoDispositivo(), Acao.ATIVAR_NOTIFICACAO);
    }

    @PostMapping("/desativar-notificacao")
    public ResponseEntity<?> desativarNotificacao(@RequestBody CodigoDispositivoDto dto) {
        String payload = dispositivoComunicacaoService.montarPayload(dto, Acao.DESATIVAR_NOTIFICACAO);
        mqttProducerService.enviarMensagem(MqttTopics.COMUNICACAO, payload);
        return dispositivoComunicacaoService.verificarSeFoiAlterado(dto.codigoDispositivo(), Acao.DESATIVAR_NOTIFICACAO);
    }
}
