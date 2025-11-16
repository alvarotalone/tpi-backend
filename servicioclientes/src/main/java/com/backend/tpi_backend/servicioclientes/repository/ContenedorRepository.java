package com.backend.tpi_backend.servicioclientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.tpi_backend.servicioclientes.model.Contenedor;

import java.util.List;

@Repository
public interface ContenedorRepository extends JpaRepository<Contenedor, Long> {
    List<Contenedor> findByIdInAndEstado_Descripcion(List<Long> ids, String descripcion);
}
