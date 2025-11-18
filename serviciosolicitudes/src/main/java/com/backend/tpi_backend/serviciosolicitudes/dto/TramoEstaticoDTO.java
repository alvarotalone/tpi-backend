package com.backend.tpi_backend.serviciosolicitudes.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TramoEstaticoDTO {
    private Long id;

    private Double latitudOrigen;
    private Double longitudOrigen;
    private Double latitudDestino;
    private Double longitudDestino;

    private String dominioCamion;

    private LocalDateTime fhInicioReal;
    private LocalDateTime fhFinReal;

    // 🔹 Ahora como String, igual que en serviciorutas
    private String tipoTramo;

    // 🔹 También como String (descripción)
    private String estadoTramo;

    private Double distanciaMetros;
}
