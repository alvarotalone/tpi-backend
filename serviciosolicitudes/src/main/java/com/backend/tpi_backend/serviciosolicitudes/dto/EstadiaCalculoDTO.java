package com.backend.tpi_backend.serviciosolicitudes.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EstadiaCalculoDTO {

    private Long idDeposito;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
