package dev.edvanronchi.mototraceapi.domain.repositories;

import dev.edvanronchi.mototraceapi.domain.entities.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {
    Optional<Dispositivo> findByCodigo(String codigo);
}
