package com.backend.tpi_backend.serviciosolicitudes.dto;

import java.util.List;

import lombok.Data;

@Data
public class CostoTotalDTO {
    private List<CostoTramoDTO> tramos;
    private List<EstadiaCalculoDTO> estadias; // puede ir vacío
}

