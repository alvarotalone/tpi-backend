package com.backend.tpi_backend.serviciotarifas.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

// Data Transfer Object
@Data
@NoArgsConstructor
public class DepositoDTO {
    
    // Los campos que nos interesan de la respuesta son estos:
    private Long id;
    private String nombre;
    private Double costoEstadiaDiario; 
}