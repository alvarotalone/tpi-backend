package com.backend.tpi_backend; // (El mismo package que tu ApiGatewayApplication)

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

@RestController
public class KeycloakCallbackController {

    private static final Logger log = LoggerFactory.getLogger(KeycloakCallbackController.class);

    // Estas variables las toma de tu application.yml
    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.redirect-uri}")
    private String redirectUri;

    private final WebClient webClient = WebClient.create();

    // ESTE ES EL ENDPOINT QUE TE DABA 404
    @GetMapping("/api/login/oauth2/code/keycloak")
    public Mono<String> intercambiarCode(
            @RequestParam(name = "code", required = false) String code) {

        log.info("🔎 Parámetro 'code' recibido: {}", code);

        if (code == null || code.isBlank()) {
            return Mono.just("Error: Keycloak no envió parámetro 'code'.");
        }

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUri);

        String tokenEndpoint = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        log.info("🔗 Llamando a token endpoint: {}", tokenEndpoint);

        return webClient.post()
                .uri(tokenEndpoint)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(JsonNode.class) // Pedimos un JsonNode
                .map(tokenResponse -> {
                    // Extraemos solo el access_token y lo devolvemos
                    if (tokenResponse != null && tokenResponse.has("access_token")) {
                        log.info("🔐 Token recibido y extraído con éxito.");
                        return tokenResponse.get("access_token").asText();
                    } else {
                        log.error("❌ La respuesta de Keycloak no contiene un 'access_token'.");
                        return "Error: La respuesta de Keycloak no contiene un 'access_token'.";
                    }
                })
                .onErrorResume(WebClientResponseException.class, e -> {
                    log.error("❌ Error HTTP al obtener token de Keycloak. Status: {}, body: {}",
                            e.getStatusCode(), e.getResponseBodyAsString());
                    return Mono.just("Error HTTP al obtener token: " + e.getResponseBodyAsString());
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("❌ Error inesperado en el callback de Keycloak", e);
                    return Mono.just("Error inesperado en callback: " + e.getMessage());
                });
    }
}