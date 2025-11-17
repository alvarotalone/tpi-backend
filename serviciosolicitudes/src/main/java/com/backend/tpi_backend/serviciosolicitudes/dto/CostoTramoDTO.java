package com.backend.tpi_backend.serviciosolicitudes.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class CostoTramoDTO {

    private Long idTipoCamion;
    private BigDecimal costoBaseKmCamion;
    private Double distanciaEnKm;
    private Double consumoCombustibleCamion;
    private LocalDate fecha;
}

