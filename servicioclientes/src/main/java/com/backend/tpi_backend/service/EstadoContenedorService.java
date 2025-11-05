package com.backend.tpi_backend.servicioclientes.service;

import com.backend.tpi_backend.servicioclientes.model.EstadoContenedor;
import com.backend.tpi_backend.servicioclientes.repository.EstadoContenedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadoContenedorService {

    private final EstadoContenedorRepository estadoRepo;

    public EstadoContenedorService(EstadoContenedorRepository estadoRepo) {
        this.estadoRepo = estadoRepo;
    }

    public List<EstadoContenedor> findAll() {
        return estadoRepo.findAll();
    }

    public Optional<EstadoContenedor> findById(Long id) {
        return estadoRepo.findById(id);
    }

    public EstadoContenedor save(EstadoContenedor estado) {
        return estadoRepo.save(estado);
    }

    public void delete(Long id) {
        estadoRepo.deleteById(id);
    }
}