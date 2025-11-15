package com.backend.tpi_backend.serviciorutas.dto;

import lombok.Data;

import java.util.List;

/**
 * Representa la respuesta básica de OSRM para /route/v1/driving
 * Solo nos interesan:
 *   - code
 *   - la primera ruta (distance, duration)
 * El resto de campos del JSON son ignorados por Jackson.
 */
@Data
public class OsrmRouteResponse {

    private String code;
    private List<Route> routes;

    @Data
    public static class Route {
        private double distance; // en metros
        private double duration; // en segundos
        // ignoramos legs/weight/etc. porque no los necesitamos
    }
}
