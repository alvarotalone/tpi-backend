package com.backend.tpi_backend.serviciotarifas.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO que representa la solicitud de cálculo para una estadía.
 */
@Data
@NoArgsConstructor
public class EstadiaCalculoDTO {

    private Long idDeposito;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}