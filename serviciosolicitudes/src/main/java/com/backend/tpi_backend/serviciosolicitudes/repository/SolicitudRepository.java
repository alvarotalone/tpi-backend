package com.backend.tpi_backend.serviciosolicitudes.repository;

import com.backend.tpi_backend.serviciosolicitudes.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    boolean existsByNumero(Long numero);
}
