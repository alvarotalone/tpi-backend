package com.backend.tpi_backend.serviciorutas.dto;

import lombok.Data;

@Data
public class CamionDTO {
    private String dominio;
    private boolean disponible;
    private Long idTipoCamion;       // se usa como campo simple, no como FK real
    private Long idTransportista;    // idem
}

