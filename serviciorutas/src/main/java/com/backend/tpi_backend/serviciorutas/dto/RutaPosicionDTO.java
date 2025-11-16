package com.backend.tpi_backend.serviciorutas.dto;

import lombok.Data;

@Data
public class RutaPosicionDTO {
    private Long idRuta;
    private Double latitudDestino;
    private Double longitudDestino;
}
