package com.backend.tpi_backend.serviciosolicitudes.repository;

import com.backend.tpi_backend.serviciosolicitudes.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    // busca solicitudes por la descripción del estado
    List<Solicitud> findByEstado_Descripcion(String descripcion);

    // Busca segun patente y estado
    List<Solicitud> findByDominioCamionAndEstado_DescripcionIn(
            String dominioCamion,
            List<String> descripcionesEstados
    );

}
