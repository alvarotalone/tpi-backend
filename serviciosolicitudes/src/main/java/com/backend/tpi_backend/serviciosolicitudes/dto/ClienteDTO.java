package com.backend.tpi_backend.serviciosolicitudes.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
}
