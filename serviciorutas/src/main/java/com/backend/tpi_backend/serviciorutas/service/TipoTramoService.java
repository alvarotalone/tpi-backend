package com.backend.tpi_backend.serviciorutas.service;

import com.backend.tpi_backend.serviciorutas.model.TipoTramo;
import com.backend.tpi_backend.serviciorutas.repository.TipoTramoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoTramoService {

    private final TipoTramoRepository repository;

    public TipoTramoService(TipoTramoRepository repository) {
        this.repository = repository;
    }

    public List<TipoTramo> getAll() { return repository.findAll(); }

    public Optional<TipoTramo> getById(Long id) { return repository.findById(id); }

    public TipoTramo create(TipoTramo tramo) { return repository.save(tramo); }

    public TipoTramo update(Long id, TipoTramo updated) {
        return repository.findById(id).map(t -> {
            updated.setId(id);
            return repository.save(updated);
        }).orElseThrow(() -> new RuntimeException("TipoTramo no encontrado con id " + id));
    }

    public void delete(Long id) { repository.deleteById(id); }
}
