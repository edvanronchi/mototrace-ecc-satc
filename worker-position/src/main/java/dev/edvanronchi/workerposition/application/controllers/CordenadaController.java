package dev.edvanronchi.workerposition.application.controllers;


import dev.edvanronchi.workerposition.application.services.CordenadaService;
import dev.edvanronchi.workerposition.domain.entities.Cordenada;
import jakarta.websocket.server.PathParam;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/cordenadas")
public class CordenadaController {

    private final CordenadaService service;

    public CordenadaController(CordenadaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Cordenada>> findAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "dataHora");
        List<Cordenada> cordenadas = service.findAll(sort);
        return new ResponseEntity<>(cordenadas, HttpStatus.OK);
    }

    @GetMapping("/ultimas-cordenadas")
    public ResponseEntity<List<Cordenada>> findUltimaCordenada(@PathParam("dispositivosCodigo") String dispositivosCodigo) {
        List<String> codigos = List.of(dispositivosCodigo.split(","));
        List<Cordenada> cordenadas = service.findAllUltimasCordenadas(codigos);
        return new ResponseEntity<>(cordenadas, HttpStatus.OK);
    }
}
