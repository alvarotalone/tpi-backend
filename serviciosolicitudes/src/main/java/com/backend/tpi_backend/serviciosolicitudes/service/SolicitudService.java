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

    // 🔹 Listar todas las solicitudes
    public List<Solicitud> findAll() {
        return repo.findAll();
    }

    // 🔹 Buscar solicitud por ID
    public Optional<Solicitud> findById(Long id) {
        return repo.findById(id);
    }

    // 🔹 Guardar una nueva solicitud
    public Solicitud save(Solicitud solicitud) {
        return repo.save(solicitud);
    }

    // 🔹 Eliminar una solicitud por ID
    public void delete(Long id) {
        repo.deleteById(id);
    }

    // 🔹 Actualizar el estado de una solicitud
    public Solicitud updateEstado(Long id, Long idEstado) {
        Optional<Solicitud> optSolicitud = repo.findById(id);
        if (optSolicitud.isEmpty()) return null;

        Solicitud solicitud = optSolicitud.get();
        EstadoSolicitud estado = estadoRepo.findById(idEstado).orElse(null);
        if (estado == null) return null;

        solicitud.setEstado(estado);
        return repo.save(solicitud);
    }
}
