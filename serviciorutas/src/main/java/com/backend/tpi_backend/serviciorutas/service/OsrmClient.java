package com.backend.tpi_backend.serviciorutas.service;

import com.backend.tpi_backend.serviciorutas.dto.OsrmRouteResponse;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente HTTP para comunicarse con el servidor OSRM.
 * Encapsula la llamada a /route/v1/driving y devuelve
 * la PRIMERA ruta sugerida (distance/duration).
 */
@Service
public class OsrmClient {

    private final RestTemplate restTemplate;

    @Value("${osrm.url}")
    private String osrmBaseUrl;

    public OsrmClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    /**
     * Obtiene la ruta entre dos puntos (en orden LON,LAT como requiere OSRM).
     *
     * @param latOrigen    latitud del origen
     * @param lonOrigen    longitud del origen
     * @param latDestino   latitud del destino
     * @param lonDestino   longitud del destino
     * @return primera ruta devuelta por OSRM (distance en metros, duration en segundos)
     */
    public OsrmRouteResponse.Route obtenerRuta(
            double latOrigen, double lonOrigen,
            double latDestino, double lonDestino) {

        // OJO: OSRM espera LON,LAT (primero longitud, luego latitud)
        String url = String.format(
                Locale.US,
                "%s/route/v1/driving/%f,%f;%f,%f?overview=false",
                osrmBaseUrl,
                lonOrigen, latOrigen,
                lonDestino, latDestino
        );


        OsrmRouteResponse response =
                restTemplate.getForObject(url, OsrmRouteResponse.class);

        if (response == null || response.getRoutes() == null || response.getRoutes().isEmpty()) {
            throw new IllegalStateException("OSRM no devolvió rutas válidas para la URL: " + url);
        }

        if (!"Ok".equalsIgnoreCase(response.getCode())) {
            throw new IllegalStateException("OSRM devolvió código: " + response.getCode());
        }

        // Nos quedamos con la primera ruta
        return response.getRoutes().get(0);
    }
}
