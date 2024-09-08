package dev.edvanronchi.workerposition.domain.models;

import dev.edvanronchi.workerposition.domain.enums.Acao;
import dev.edvanronchi.workerposition.domain.enums.Origem;
import dev.edvanronchi.workerposition.domain.enums.Tipo;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Mensagem {

    private String codigoDispositivo;
    private Origem origem;
    private Acao acao;
    private Tipo tipo;
}
