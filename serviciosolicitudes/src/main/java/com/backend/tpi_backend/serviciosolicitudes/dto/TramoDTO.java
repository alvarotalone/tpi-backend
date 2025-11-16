package com.backend.tpi_backend.serviciosolicitudes.dto;

import lombok.Data;

@Data
public class TramoDTO {
    private Long id;
    private Double latitudOrigen;
    private Double longitudOrigen;
    private Double latitudDestino;
    private Double longitudDestino;
    private String dominioCamion;
}
