package com.backend.tpi_backend.serviciorutas.repository;

import com.backend.tpi_backend.serviciorutas.model.EstadoTramo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoTramoRepository extends JpaRepository<EstadoTramo, Long> {
    Optional<EstadoTramo> findByDescripcion(String descripcion);
}
