// EstadiaCalculoDTO.java
package com.backend.tpi_backend.serviciorutas.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class EstadiaCalculoDTO {
    private Long idDeposito;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
