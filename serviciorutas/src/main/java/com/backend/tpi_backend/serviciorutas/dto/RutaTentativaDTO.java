package com.backend.tpi_backend.serviciorutas.dto;

import lombok.Data;

@Data
public class RutaTentativaDTO {
    private Long idRuta;
    private double distancia;
    private double duracion;
    private String descripcion;
}
