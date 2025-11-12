package com.backend.tpi_backend.serviciorutas.service;

import com.backend.tpi_backend.serviciorutas.model.Tramo;
import com.backend.tpi_backend.serviciorutas.repository.TramoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TramoService {

    private final TramoRepository repository;

    public TramoService(TramoRepository repository) {
        this.repository = repository;
    }

    public List<Tramo> getAll() { return repository.findAll(); }

    public Optional<Tramo> getById(Long id) { return repository.findById(id); }

    public Tramo create(Tramo tramo) { return repository.save(tramo); }

    public Tramo update(Long id, Tramo updated) {
        return repository.findById(id).map(t -> {
            updated.setId(id);
            return repository.save(updated);
        }).orElseThrow(() -> new RuntimeException("Tramo no encontrado con id " + id));
    }

    public void delete(Long id) { repository.deleteById(id); }
}
