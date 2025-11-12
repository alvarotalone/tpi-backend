package com.backend.tpi_backend.serviciosolicitudes.service;

import com.backend.tpi_backend.serviciosolicitudes.model.EstadoSolicitud;
import com.backend.tpi_backend.serviciosolicitudes.repository.EstadoSolicitudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadoSolicitudService {

    private final EstadoSolicitudRepository repo;

    public EstadoSolicitudService(EstadoSolicitudRepository repo) {
        this.repo = repo;
    }

    public List<EstadoSolicitud> findAll() { return repo.findAll(); }
    public Optional<EstadoSolicitud> findById(Long id) { return repo.findById(id); }
    public EstadoSolicitud save(EstadoSolicitud e) { return repo.save(e); }
    public void delete(Long id) { repo.deleteById(id); }
}
