package com.backend.tpi_backend.serviciousuarios.service;

import com.backend.tpi_backend.serviciousuarios.dto.LoginRequest;
import com.backend.tpi_backend.serviciousuarios.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

    private final RestTemplate restTemplate;

    @Value("${integracion.keycloak.url}")
    private String integracionKeycloakUrl;

    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TokenResponse login(LoginRequest request) {

        // delega al micro de integración
        String url = integracionKeycloakUrl + "/keycloak/login";

        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                url,
                request,
                TokenResponse.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Error al obtener token de Keycloak");
        }

        return response.getBody();
    }
}
