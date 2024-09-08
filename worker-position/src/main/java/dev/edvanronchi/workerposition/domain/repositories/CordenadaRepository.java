package dev.edvanronchi.workerposition.domain.repositories;

import dev.edvanronchi.workerposition.domain.entities.Cordenada;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CordenadaRepository extends MongoRepository<Cordenada, String> {

        List<Cordenada> findAllByCodigoDispositivo(String codigoDispositivo);
        Optional<Cordenada> findFirstByCodigoDispositivoOrderByDataHoraDesc(String codigoDispositivo);
}
