package com.backend.tpi_backend.serviciodepositos.repositories;

import com.backend.tpi_backend.serviciodepositos.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {
}