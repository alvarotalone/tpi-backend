package com.backend.tpi_backend.serviciocamiones.repository;

import com.backend.tpi_backend.serviciocamiones.model.DetalleDisponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleDisponibilidadRepository extends JpaRepository<DetalleDisponibilidad, Long> {

    // Listar todos los detalles de un camión por dominio
    List<DetalleDisponibilidad> findByCamion_Dominio(String dominio);
}
