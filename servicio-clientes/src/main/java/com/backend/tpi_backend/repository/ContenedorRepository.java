package com.backend.tpi_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.tpi_backend.model.Contenedor;

@Repository
public interface ContenedorRepository extends JpaRepository<Contenedor, Long> {
}
