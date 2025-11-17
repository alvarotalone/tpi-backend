package com.backend.tpi_backend.serviciorutas.dto;

import lombok.Data;

@Data
public class CamionDTO {

    private String dominio;

    // Datos de disponibilidad si querés mantenerlos
    private boolean disponible;
    private Long idTransportista;

    // 🔹 Datos técnicos obligatorios (se traen de TipoCamion)
    private Long idTipoCamion;
    private Double capacidadPesoKg;
    private Double capacidadVolumenM3;
    private Double costoBaseKm;
    private Double consumoCombustibleKm;
}


