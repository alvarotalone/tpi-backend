package com.backend.tpi_backend;

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

import java.util.HashMap;
import java.util.Map;

@RestController
public class KeycloakCallbackController {

    private static final Logger log = LoggerFactory.getLogger(KeycloakCallbackController.class);

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.redirect-uri}")
    private String redirectUri;

    private final WebClient webClient = WebClient.create();

    @GetMapping("/api/login/oauth2/code/keycloak")
    public Mono<Map<String, Object>> intercambiarCode(
            @RequestParam(name = "code", required = false) String code) {

        Map<String, Object> respuesta = new HashMap<>();

        log.info("🔎 Parámetro 'code' recibido: {}", code);

        if (code == null || code.isBlank()) {
            respuesta.put("error", "Keycloak no envió parámetro 'code'.");
            return Mono.just(respuesta);
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
                .bodyToMono(String.class) // respuesta cruda de Keycloak
                .map(body -> {
                    log.info("🔐 Token recibido desde Keycloak: {}", body);

                    Map<String, Object> ok = new HashMap<>();
                    ok.put("mensaje", "Token recibido y logueado en consola del API Gateway.");
                    ok.put("token_raw", body); // por si querés verlo en el navegador
                    return ok;
                })
                .onErrorResume(WebClientResponseException.class, e -> {
                    log.error("❌ Error HTTP al obtener token de Keycloak. Status: {}, body: {}",
                            e.getStatusCode(), e.getResponseBodyAsString());

                    Map<String, Object> err = new HashMap<>();
                    err.put("error", "Error HTTP al obtener token de Keycloak");
                    err.put("status", e.getStatusCode().value());
                    err.put("body", e.getResponseBodyAsString());
                    return Mono.just(err);
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("❌ Error inesperado en el callback de Keycloak", e);

                    Map<String, Object> err = new HashMap<>();
                    err.put("error", "Error inesperado en callback");
                    err.put("detalle", e.getMessage());
                    return Mono.just(err);
                });
    }
}
