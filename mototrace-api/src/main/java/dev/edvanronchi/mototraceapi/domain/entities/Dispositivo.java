package dev.edvanronchi.mototraceapi.domain.entities;

import dev.edvanronchi.mototraceapi.domain.enums.Cor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DISPOSITIVOS")
public class Dispositivo {

    @Id
    @Column(name = "ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "O campo 'codigo' não pode ser nulo")
    @Column(name = "CODIGO")
    private String codigo;

    @NotEmpty(message = "O campo 'nome' deve ser preenchido")
    @Column(name = "NOME")
    private String nome;

    @NotEmpty(message = "O campo 'descricao' deve ser preenchido")
    @Column(name = "DESCRICAO")
    private String descricao;

    @Column(name = "COR")
    private Cor cor = Cor.AZUL;

    @Column(name = "BLOQUEADO")
    private boolean bloqueado = false;

    @Column(name = "NOTIFICACAO_ATIVA")
    private boolean notificacaoAtiva = false;
}
