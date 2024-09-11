package dev.edvanronchi.workerposition.application.services;

import dev.edvanronchi.workerposition.domain.entities.Coordenada;
import dev.edvanronchi.workerposition.domain.repositories.CoordenadaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CoordenadaService {

    private final CoordenadaRepository repository;

    public CoordenadaService(CoordenadaRepository repository) {
        this.repository = repository;
    }

    public List<Coordenada> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    public List<Coordenada> findAllUltimasCoordenadas(List<String> codigos) {
        List<Coordenada> ultimasCoordenadas = new ArrayList<>();

        for (String codigo : codigos) {
            Optional<Coordenada> coordenada = repository.findFirstByCodigoDispositivoOrderByDataHoraDesc(codigo);
            coordenada.ifPresent(ultimasCoordenadas::add);
        }
        return ultimasCoordenadas;
    }

    public void save(Coordenada coordenada) {
        repository.save(coordenada);
    }
}
