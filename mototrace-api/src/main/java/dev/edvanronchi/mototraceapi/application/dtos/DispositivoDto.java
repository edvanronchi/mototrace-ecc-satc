package dev.edvanronchi.mototraceapi.application.dtos;

import dev.edvanronchi.mototraceapi.domain.enums.Cor;
import dev.edvanronchi.mototraceapi.domain.entities.Dispositivo;

public record DispositivoDto(Long id, String codigo, String nome, String descricao, Cor cor) {
    public Dispositivo toEntity() {
        return Dispositivo.builder()
                .id(id)
                .codigo(codigo)
                .nome(nome)
                .descricao(descricao)
                .cor(cor)
                .build();
    }
}
