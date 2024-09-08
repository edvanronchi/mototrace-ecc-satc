package dev.edvanronchi.workerposition.application.services;

import dev.edvanronchi.workerposition.domain.entities.Cordenada;
import dev.edvanronchi.workerposition.domain.repositories.CordenadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CordenadaService {

    private final CordenadaRepository repository;

    public CordenadaService(CordenadaRepository repository) {
        this.repository = repository;
    }

    public List<Cordenada> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    public List<Cordenada> findAllUltimasCordenadas(List<String> codigos) {
        List<Cordenada> ultimasCordenadas = new ArrayList<>();

        for (String codigo : codigos) {
            Optional<Cordenada> cordenada = repository.findFirstByCodigoDispositivoOrderByDataHoraDesc(codigo);
            cordenada.ifPresent(ultimasCordenadas::add);
        }
        return ultimasCordenadas;
    }

    public void save(Cordenada cordenada) {
        repository.save(cordenada);
    }
}
