package dev.edvanronchi.workerposition.domain.repositories;

import dev.edvanronchi.workerposition.domain.entities.Coordenada;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CoordenadaRepository extends MongoRepository<Coordenada, String> {

        List<Coordenada> findAllByCodigoDispositivo(String codigoDispositivo);
        Optional<Coordenada> findFirstByCodigoDispositivoOrderByDataHoraDesc(String codigoDispositivo);
}
