package com.backend.tpi_backend.serviciousuarios.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfig {

    // 1. Leemos las propiedades que pusimos en application.properties
    @Value("${keycloak.admin.server-url}")
    private String serverUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.username}")
    private String username;

    @Value("${keycloak.admin.password}")
    private String password;

    // 2. Creamos la "herramienta" (el Bean)
    @Bean
    public Keycloak keycloakAdminClient() {
        // Usamos el KeycloakBuilder (de la dependencia que agregamos)
        // para construir el cliente de admin
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm) // El realm "master" para loguearnos como admin
                .clientId(clientId) // El cliente "admin-cli"
                .username(username) // El usuario "admin"
                .password(password) // La clave "admin123"
                .grantType("password") // Le decimos que nos logueamos con usuario y clave
                .build();
    }
}