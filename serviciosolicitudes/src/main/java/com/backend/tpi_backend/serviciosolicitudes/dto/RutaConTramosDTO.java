package com.backend.tpi_backend.serviciosolicitudes.dto;

import lombok.Data;
import java.util.List;

@Data
public class RutaConTramosDTO {

    private Long idSolicitud;
    private Long idRuta;
    private String estadoSolicitud;      // PROGRAMADA, EN_TRANSITO, etc.
    private List<TramoEstaticoDTO> tramos;       // Reusás el TramoDTO del servicio rutas (o una copia igual)
}
