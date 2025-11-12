package com.backend.tpi_backend.serviciosolicitudes.service;

import com.backend.tpi_backend.serviciosolicitudes.model.EstadoSolicitud;
import com.backend.tpi_backend.serviciosolicitudes.model.Solicitud;
import com.backend.tpi_backend.serviciosolicitudes.repository.EstadoSolicitudRepository;
import com.backend.tpi_backend.serviciosolicitudes.repository.SolicitudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SolicitudService {

    private final SolicitudRepository repo;
    private final EstadoSolicitudRepository estadoRepo;

    public SolicitudService(SolicitudRepository repo, EstadoSolicitudRepository estadoRepo) {
        this.repo = repo;
        this.estadoRepo = estadoRepo;
    }

    public List<Solicitud> findAll() { return repo.findAll(); }
    public Optional<Solicitud> findByNumero(Long numero) { return repo.findById(numero); }
    public Solicitud save(Solicitud s) { return repo.save(s); }
    public void delete(Long numero) { repo.deleteById(numero); }

    public Solicitud updateEstado(Long numero, Long idEstado) {
        Optional<Solicitud> opt = repo.findById(numero);
        if (opt.isEmpty()) return null;
        Solicitud s = opt.get();
        EstadoSolicitud est = estadoRepo.findById(idEstado).orElse(null);
        if (est == null) return null;
        s.setEstado(est);
        return repo.save(s);
    }
}
