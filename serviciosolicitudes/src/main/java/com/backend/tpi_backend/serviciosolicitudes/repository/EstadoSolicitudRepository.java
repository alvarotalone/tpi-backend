package com.backend.tpi_backend.serviciosolicitudes.repository;

import com.backend.tpi_backend.serviciosolicitudes.model.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoSolicitudRepository extends JpaRepository<EstadoSolicitud, Long> {

    // 🔹 Agregado para buscar el estado "BORRADOR"
    Optional<EstadoSolicitud> findByDescripcion(String descripcion);
}
