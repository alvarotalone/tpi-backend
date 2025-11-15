package com.backend.tpi_backend.serviciointegracionkeycloak.service;

import com.backend.tpi_backend.serviciointegracionkeycloak.dto.LoginRequest;
import com.backend.tpi_backend.serviciointegracionkeycloak.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KeycloakAuthService {

    @Value("${keycloak.auth-url}")
    private String authUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public TokenResponse login(LoginRequest request) {

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("username", request.getUsername());
        form.add("password", request.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        ResponseEntity<TokenResponse> response =
                restTemplate.postForEntity(authUrl, entity, TokenResponse.class);

        return response.getBody();
    }
}
