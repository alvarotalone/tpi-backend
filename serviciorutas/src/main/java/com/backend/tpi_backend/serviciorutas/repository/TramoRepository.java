package com.backend.tpi_backend.serviciorutas.repository;

import com.backend.tpi_backend.serviciorutas.model.Tramo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TramoRepository extends JpaRepository<Tramo, Long> {
    // Buscar tramos por ruta
    List<Tramo> findByRuta_Id(Long idRuta);

    // Buscar tramos asignados a un camión específico (para transportista)
    List<Tramo> findByDominioCamion(String dominioCamion);

    // Buscar tramos por estado + camión (muy útil) - POR LAS DUDAS
    List<Tramo> findByDominioCamionAndEstadoTramo_Id(String dominio, Long idEstado);
}
