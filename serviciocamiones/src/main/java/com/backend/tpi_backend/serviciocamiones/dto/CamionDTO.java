package com.backend.tpi_backend.serviciocamiones.dto;

import lombok.Data;

@Data
public class CamionDTO {

    private String dominio;

    // 🔹 Datos técnicos provenientes de TipoCamion
    private Long idTipoCamion;
    private Double capacidadPeso;
    private Double capacidadVolumen;
    private Double costoBaseKm;
    private Double consumoCombustible;

    // 🔹 Datos del transportista (opcionales pero útiles)
    private Long idTransportista;
    private String nombreTransportista;
}
