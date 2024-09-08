package dev.edvanronchi.mototraceapi.application.controllers;

import dev.edvanronchi.mototraceapi.application.dtos.AtualizarSituacaoDto;
import dev.edvanronchi.mototraceapi.application.dtos.DispositivoDto;
import dev.edvanronchi.mototraceapi.application.services.DispositivoService;
import dev.edvanronchi.mototraceapi.domain.entities.Dispositivo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/dispositivos")
public class DispositivoController {

    @Autowired
    private DispositivoService service;

    @GetMapping("/{id}")
    public ResponseEntity<Dispositivo> find(@PathVariable Long id) {
        Dispositivo dispositivo = service.find(id);
        return new ResponseEntity<>(dispositivo, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Dispositivo>> findAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "nome");
        List<Dispositivo> dispositivos = service.findAll(sort);
        return new ResponseEntity<>(dispositivos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Dispositivo> create(@RequestBody DispositivoDto dispositivoDto) {
        Dispositivo dispositivo = service.save(dispositivoDto.toEntity());
        return new ResponseEntity<>(dispositivo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dispositivo> update(@PathVariable Long id, @RequestBody DispositivoDto dto) {
        Dispositivo dispositivo = service.update(id, dto.toEntity());
        return new ResponseEntity<>(dispositivo, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Objects> delete(@PathVariable Long id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{codigo}/atualizar-situacao")
    public ResponseEntity<Dispositivo> atualizarSituacao(@PathVariable String codigo, @RequestBody AtualizarSituacaoDto dto) {
        Dispositivo dispositivo = service.atualizarSituacao(codigo, dto);
        return new ResponseEntity<>(dispositivo, HttpStatus.OK);
    }

    @GetMapping("/{codigo}/bloqueado")
    public ResponseEntity<Boolean> isBloqueado(@PathVariable String codigo) {
        boolean resultado = service.isBloqueado(codigo);
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    @GetMapping("/{codigo}/notificacao-ativa")
    public ResponseEntity<Boolean> isNotificacaoAtiva(@PathVariable String codigo) {
        boolean resultado = service.isNotificacaoAtiva(codigo);
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }
}
