package com.backend.tpi_backend.serviciosolicitudes.dto.clientes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContenedorDTO {

    private Long id;
    private Double peso;
    private Double volumen;

    // solo necesitamos referencia al cliente
    private ClienteDTO cliente;
}
