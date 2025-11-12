package com.backend.tpi_backend.serviciorutas.service;

import com.backend.tpi_backend.serviciorutas.model.Ruta;
import com.backend.tpi_backend.serviciorutas.repository.RutaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RutaService {

    private final RutaRepository repository;

    public RutaService(RutaRepository repository) {
        this.repository = repository;
    }

    public List<Ruta> getAll() { return repository.findAll(); }

    public Optional<Ruta> getById(Long id) { return repository.findById(id); }

    public Ruta create(Ruta Ruta) { return repository.save(Ruta); }

    public Ruta update(Long id, Ruta updated) {
        return repository.findById(id).map(t -> {
            updated.setId(id);
            return repository.save(updated);
        }).orElseThrow(() -> new RuntimeException("Ruta no encontrado con id " + id));
    }

    public void delete(Long id) { repository.deleteById(id); }
}
