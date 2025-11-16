package com.backend.tpi_backend.serviciorutas.dto;

import lombok.Data;

@Data
public class DepositoDTO {
    private Long id;
    private String nombre;
    private Double costoEstadiaDiario;
    private UbicacionDTO ubicacion;
}
