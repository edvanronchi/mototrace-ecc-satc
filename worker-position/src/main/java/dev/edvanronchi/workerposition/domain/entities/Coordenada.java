package dev.edvanronchi.workerposition.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "coordenadas")
public class Coordenada {

    @Id
    private String id;
    private String codigoDispositivo;
    private Double latitude;
    private Double longitude;
    private LocalDateTime dataHora = LocalDateTime.now();
    private String codigoDispositivoAssociado;
}
