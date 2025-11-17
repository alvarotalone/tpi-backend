// TramoDTO.java
package com.backend.tpi_backend.serviciorutas.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TramoDTO {
    private Long id;
    private Double latitudOrigen;
    private Double longitudOrigen;
    private Double latitudDestino;
    private Double longitudDestino;

    private String tipoTramo;      // descripcion tipo ("origen-deposito", etc.)
    private String estadoTramo;    // descripcion estado ("Pendiente", "En curso", "Finalizado")

    private String dominioCamion;

    private Double distanciaMetros; // calculada vía OSRM

    private LocalDateTime fhInicioReal;
    private LocalDateTime fhFinReal;
}
