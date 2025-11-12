package com.backend.tpi_backend.serviciosolicitudes.repository;

import com.backend.tpi_backend.serviciosolicitudes.model.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoSolicitudRepository extends JpaRepository<EstadoSolicitud, Long> {
}
