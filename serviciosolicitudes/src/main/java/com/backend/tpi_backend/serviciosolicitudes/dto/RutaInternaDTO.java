package com.backend.tpi_backend.serviciosolicitudes.dto;

import lombok.Data;

@Data
public class RutaInternaDTO {
    private Long id;
    private Integer cantidadTramos;
    private Integer cantidadDepositos;
    private Integer duracionEstimada;
}
