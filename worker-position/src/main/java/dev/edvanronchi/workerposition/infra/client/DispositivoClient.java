package dev.edvanronchi.workerposition.infra.client;

import dev.edvanronchi.workerposition.application.dtos.AtualizarSituacaoDto;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name= "DispositivoClient", url = "${variaveis.feign.mototrace-api.url}/dispositivos")
public interface DispositivoClient {

    @RequestMapping(method = RequestMethod.GET, value = "/{codigo}/bloqueado")
    ResponseEntity<Boolean> isBloqueado(@PathVariable("codigo") String codigo);

    @RequestMapping(method = RequestMethod.GET, value = "/{codigo}/notificacao-ativa")
    ResponseEntity<Boolean> isNotificacaoAtiva(@PathVariable("codigo") String codigo);

    @RequestMapping(method = RequestMethod.PUT, value = "/{codigo}/atualizar-situacao", consumes = "application/json")
    Response atualizarSituacao(@PathVariable("codigo") String codigo, AtualizarSituacaoDto situacao);
}
