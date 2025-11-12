package com.backend.tpi_backend.serviciorutas.service;

import com.backend.tpi_backend.serviciorutas.model.EstadoTramo;
import com.backend.tpi_backend.serviciorutas.repository.EstadoTramoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadoTramoService {

    private final EstadoTramoRepository repository;

    public EstadoTramoService(EstadoTramoRepository repository) {
        this.repository = repository;
    }

    public List<EstadoTramo> getAll() { return repository.findAll(); }

    public Optional<EstadoTramo> getById(Long id) { return repository.findById(id); }

    public EstadoTramo create(EstadoTramo EstadoTramo) { return repository.save(EstadoTramo); }

    public EstadoTramo update(Long id, EstadoTramo updated) {
        return repository.findById(id).map(t -> {
            updated.setId(id);
            return repository.save(updated);
        }).orElseThrow(() -> new RuntimeException("EstadoTramo no encontrado con id " + id));
    }

    public void delete(Long id) { repository.deleteById(id); }
}
