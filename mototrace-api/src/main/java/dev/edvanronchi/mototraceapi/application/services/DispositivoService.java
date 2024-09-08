package dev.edvanronchi.mototraceapi.application.services;

import dev.edvanronchi.mototraceapi.application.dtos.AtualizarSituacaoDto;
import dev.edvanronchi.mototraceapi.domain.entities.Dispositivo;
import dev.edvanronchi.mototraceapi.domain.exceptions.NotFoundException;
import dev.edvanronchi.mototraceapi.domain.repositories.DispositivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DispositivoService {

    @Autowired
    private DispositivoRepository repository;

    public DispositivoService(DispositivoRepository repository) {
        this.repository = repository;
    }

    public List<Dispositivo> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    public Dispositivo find(Long id) {
        Optional<Dispositivo> dispositivo = repository.findById(id);
        if (dispositivo.isEmpty()) {
            throw new NotFoundException("Não foi possível encontrar o dispositivo com o id " + id);
        }
        return dispositivo.get();
    }

    public Dispositivo findByCodigo(String codigo) {
        Optional<Dispositivo> dispositivo = repository.findByCodigo(codigo);
        if (dispositivo.isEmpty()) {
            throw new NotFoundException("Não foi possível encontrar o dispositivo com o codigo " + codigo);
        }
        return dispositivo.get();
    }

    public Dispositivo save(Dispositivo dispositivo) {
        return repository.save(dispositivo);
    }

    public Dispositivo update(Long id, Dispositivo dispositivo) {
        Dispositivo dispositivoOld = find(id);

        dispositivoOld.setCodigo(dispositivo.getCodigo());
        dispositivoOld.setNome(dispositivo.getNome());
        dispositivoOld.setDescricao(dispositivo.getDescricao());
        dispositivoOld.setCor(dispositivo.getCor());

        return repository.save(dispositivoOld);
    }

    public void delete(Long id) {
        Dispositivo dispositivo = find(id);
        repository.delete(dispositivo);
    }

    public Dispositivo atualizarSituacao(String codigo, AtualizarSituacaoDto dto) {
        Dispositivo dispostivo = findByCodigo(codigo);

        switch (dto.acao()) {
            case BLOQUEAR:
                dispostivo.setBloqueado(true);
                break;
            case DESBLOQUEAR:
                dispostivo.setBloqueado(false);
                break;
            case ATIVAR_NOTIFICACAO:
                dispostivo.setNotificacaoAtiva(true);
                break;
            case DESATIVAR_NOTIFICACAO:
                dispostivo.setNotificacaoAtiva(false);
                break;
        }

        return repository.save(dispostivo);
    }

    public boolean isBloqueado(String codigo) {
        Dispositivo dispositivo = findByCodigo(codigo);
        return dispositivo.isBloqueado();
    }

    public boolean isNotificacaoAtiva(String codigo) {
        Dispositivo dispositivo = findByCodigo(codigo);
        return dispositivo.isNotificacaoAtiva();
    }
}
