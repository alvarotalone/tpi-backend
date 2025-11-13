package com.backend.tpi_backend.serviciosolicitudes.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitudRequestDTO {

    private Long idCliente;
    private Long idContenedor;

    private Double latitudOrigen;
    private Double longitudOrigen;
    private Double latitudDestino;
    private Double longitudDestino;

    // opcionales por ahora
    private Double costoEstimado;
    private Double tiempoEstimado;

    // 🔹 Datos para crear cliente si no existe
    private String nombreCliente;
    private String apellidoCliente;
    private String telefonoCliente;
    private String emailCliente;

    // 🔹 Datos para crear contenedor si no existe
    private Double pesoContenedor;
    private Double volumenContenedor;
}
