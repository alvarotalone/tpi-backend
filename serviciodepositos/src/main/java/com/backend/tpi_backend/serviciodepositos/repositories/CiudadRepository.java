package com.backend.tpi_backend.serviciodepositos.repositories;

import com.backend.tpi_backend.serviciodepositos.model.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CiudadRepository extends JpaRepository<Ciudad, Long> {
    // Ej: Buscar ciudades por provincia
    // List<Ciudad> findByProvinciaId(Long provinciaId);
}