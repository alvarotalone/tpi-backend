package com.backend.tpi_backend.serviciotarifas.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO que representa la solicitud de cálculo de costo para un tramo.
 * Esto es lo que ServicioSolicitudes (u otro) nos enviará.
 */
@Data
@NoArgsConstructor
public class CostoTramoDTO {

    //Datos del Tramo/Camión

    private Double distanciaEnKm;

    private Double consumoCombustibleCamion;
    
    private BigDecimal costoBaseKmCamion;

    // --- Datos para buscar la Tarifa ---

    private Long idTipoCamion;

    private LocalDate fecha;
}