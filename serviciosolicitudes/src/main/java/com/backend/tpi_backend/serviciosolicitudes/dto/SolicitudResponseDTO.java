package com.backend.tpi_backend.serviciosolicitudes.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitudResponseDTO {

    private Long idSolicitud;

    private Long idCliente;
    private Long idContenedor;

    private Double latitudOrigen;
    private Double longitudOrigen;

    private Double latitudDestino;
    private Double longitudDestino;

    private Double costoEstimado;
    private Double tiempoEstimado;

    private String estadoSolicitud;
}
