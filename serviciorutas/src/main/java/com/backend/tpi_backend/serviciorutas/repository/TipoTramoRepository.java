package com.backend.tpi_backend.serviciorutas.repository;

import com.backend.tpi_backend.serviciorutas.model.TipoTramo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoTramoRepository extends JpaRepository<TipoTramo, Long> {

    Optional<TipoTramo> findByDescripcion(String descripcion);

}
