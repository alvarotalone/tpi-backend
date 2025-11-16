package com.backend.tpi_backend.serviciosolicitudes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContenedorUbicacionDTO {

    private Long idContenedor;
    private Long idRuta;
    private Double latitudDestino;
    private Double longitudDestino;
}
