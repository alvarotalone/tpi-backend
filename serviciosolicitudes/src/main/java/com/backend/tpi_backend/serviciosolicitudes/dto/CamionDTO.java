package com.backend.tpi_backend.serviciosolicitudes.dto;

import lombok.Data;

@Data
public class CamionDTO {
    private String dominio;
    private Double capacidadPesoKg;
    private Double capacidadVolumenM3;
    private Double consumoCombustibleKm;
    private Double costoKm;
    private Boolean disponibilidad;
    private Long idTipoCamion;
}

