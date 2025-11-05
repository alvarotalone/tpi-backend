package com.backend.tpi_backend.servicioclientes.repository;

import com.backend.tpi_backend.servicioclientes.model.EstadoContenedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoContenedorRepository extends JpaRepository<EstadoContenedor, Long> {
}