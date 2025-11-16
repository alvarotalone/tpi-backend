package com.backend.tpi_backend.serviciosolicitudes.dto;

import lombok.Data;

@Data
public class RutaDTO {
    private Long id;
    private Double distancia;
    private Integer duracionEstimada;   // minutos
}

