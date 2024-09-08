package dev.edvanronchi.workerposition.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.edvanronchi.workerposition.application.dtos.CodigoDispositivoDto;
import dev.edvanronchi.workerposition.domain.enums.Acao;
import dev.edvanronchi.workerposition.domain.enums.Origem;
import dev.edvanronchi.workerposition.domain.models.Mensagem;
import dev.edvanronchi.workerposition.infra.client.DispositivoClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class DispositivoComunicacaoService {
    private static final int MAXIMO_TENTATIVAS = 2;
    private static final int TEMPO_REQUISICAO = 1000;
    private static final Logger LOGGER = LogManager.getLogger(DispositivoComunicacaoService.class);

    private final DispositivoClient dispositivoClient;

    public DispositivoComunicacaoService(DispositivoClient dispositivoClient) {
        this.dispositivoClient = dispositivoClient;
    }

    public String montarPayload(CodigoDispositivoDto dto, Acao acao) {
        ObjectMapper objectMapper = new ObjectMapper();
        Mensagem mensagem = new Mensagem();

        mensagem.setAcao(acao);
        mensagem.setOrigem(Origem.SERVIDOR);
        mensagem.setCodigoDispositivo(dto.codigoDispositivo());

        try {
            return objectMapper.writeValueAsString(mensagem);
        } catch (JsonProcessingException e) {
            LOGGER.error("e: ", e);
        }
        return null;
    }

    public ResponseEntity<?> verificarSeFoiAlterado(String codigo, Acao acao) {
        try {
            for (int tentativa = 1; tentativa <= MAXIMO_TENTATIVAS; tentativa++) {
                Thread.sleep(TEMPO_REQUISICAO);

                boolean foiAlterado = switch (acao) {
                    case BLOQUEAR -> Boolean.TRUE.equals(dispositivoClient.isBloqueado(codigo).getBody());
                    case DESBLOQUEAR -> Boolean.FALSE.equals(dispositivoClient.isBloqueado(codigo).getBody());
                    case ATIVAR_NOTIFICACAO ->
                            Boolean.TRUE.equals(dispositivoClient.isNotificacaoAtiva(codigo).getBody());
                    case DESATIVAR_NOTIFICACAO ->
                            Boolean.FALSE.equals(dispositivoClient.isNotificacaoAtiva(codigo).getBody());
                };

                if (foiAlterado) {
                    return ResponseEntity.status(HttpStatus.OK).build();
                }
            }
        } catch (InterruptedException e) {
            LOGGER.error("e: ", e);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
