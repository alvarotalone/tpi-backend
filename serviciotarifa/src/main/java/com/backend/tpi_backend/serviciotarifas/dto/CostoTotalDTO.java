package com.backend.tpi_backend.serviciotarifas.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que representa el "paquete" completo para un cálculo de costo total.
 * Esto es lo que nos enviará ServicioSolicitudes.
 */
@Data
@NoArgsConstructor
public class CostoTotalDTO {

    private List<CostoTramoDTO> tramos;
    
    private List<EstadiaCalculoDTO> estadias;
}

/*
 * Le estamos diciendo al resto del programa 
 * "Para calcular un costo total, envíame un JSON que tenga una lista de tramos y una lista de estadias."
 * 
 */