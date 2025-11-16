package com.backend.tpi_backend.serviciosolicitudes.dto;

import lombok.Data;

@Data
public class CamionDTO {
    private String dominio;
    private Double capacidad_peso_kg;
    private Double capacidad_volumen_m3;
    private Double consumo_combustible_km;
    private Double costo_km;
    private Boolean disponibilidad;
    private Long transportistaId;
}
