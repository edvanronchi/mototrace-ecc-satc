package dev.edvanronchi.workerposition.application.controllers;


import dev.edvanronchi.workerposition.application.services.CoordenadaService;
import dev.edvanronchi.workerposition.domain.entities.Coordenada;
import jakarta.websocket.server.PathParam;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/coordenadas")
public class CoordenadaController {

    private final CoordenadaService service;

    public CoordenadaController(CoordenadaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Coordenada>> findAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "dataHora");
        List<Coordenada> coordenadas = service.findAll(sort);
        return new ResponseEntity<>(coordenadas, HttpStatus.OK);
    }

    @GetMapping("/ultimas-coordenadas")
    public ResponseEntity<List<Coordenada>> findUltimaCoordenada(@PathParam("dispositivosCodigo") String dispositivosCodigo) {
        List<String> codigos = List.of(dispositivosCodigo.split(","));
        List<Coordenada> coordenadas = service.findAllUltimasCoordenadas(codigos);
        return new ResponseEntity<>(coordenadas, HttpStatus.OK);
    }
}
